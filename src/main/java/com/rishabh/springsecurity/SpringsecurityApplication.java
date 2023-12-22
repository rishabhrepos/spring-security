package com.rishabh.springsecurity;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootApplication(scanBasePackages = {"com.rishabh.springsecurity"})
@RestController
public class SpringsecurityApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(SpringsecurityApplication.class, args);
//	}

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${server.port}")
	private int serverPort;


	@GetMapping("/home")
	public String getCheck() throws IOException {

		return "/HOME";
	}

	@GetMapping("/api")
	public String postCheck() {
//		new EmbeddedDatabaseBuilder()
//			.setType(EmbeddedDatabaseType.H2)
//			.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//			.build();
		return "Hello Rishabh /API";
	}


	public static void main(String[] args) {
		List<String> token = List.of("abcd", "efgh");
		List<List<String>> req = List.of(List.of("GET","https://www.ggole.com?token=abcd&name=alex"), List.of("GET","https://www.ggole.com?token=abcde&name=alex"),  List.of("POST","https://www.ggole.com?token=abcd&name=alex"));
		String s = String.join(",",getResponses(token, req));
		System.out.println(s);
	}
	
	
	
	
	static List<String> getResponses(List<String> auth_tokens, List<List<String>> requests) {

		List<String> response= new ArrayList<>();

		for (List<String> req:  requests) {
			List<String> res = new ArrayList<>();
			if(req.get(0).equals("GET")) {
				String paramsSubString = req.get(1).substring(req.get(1).lastIndexOf("?") + 1 );
				if(!paramsSubString.contains("token")) {
					res.add("INVALID");
					continue;
				}
				String[] params = paramsSubString.split("&");
				for(String param : params) {
					String[] p = param.split("=");
					if(p[0].equals("token") && !auth_tokens.contains(p[1])) {
						res.add("INVALID");
						break;
					} else if(!p[0].equals("token")) {
						res.add(p[0]);
						res.add(p[1]);
					} else {
						res.add("VALID");
					}
				}
			} else {
				String paramsSubString = req.get(1).substring(req.get(1).lastIndexOf("?") + 1 );
				String[] params = paramsSubString.split("&");
				if(!(paramsSubString.contains("token") && paramsSubString.contains("csrf"))) {
					res.add("INVALID");
					continue;
				}
				for(String param : params) {
					String[] p = param.split("=");
					if((p[0].equals("token") && !auth_tokens.contains(p[1])) || (p[0].equals("csrf") && !Pattern.compile("[^a-zA-Z0-9]{8,}").matcher(p[1]).find())) {
						res.clear();
						res.add("INVALID");
						break;
					} else if(!p[0].equals("token")) {
						res.add(p[0]);
						res.add(p[1]);
					} else {
						res.add("VALID");
					}
				}
			}
			response.add(String.join(",", res));
		}

		return response;
	}
}
