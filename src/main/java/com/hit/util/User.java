package com.hit.util;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @XmlElement(name="username")
    private String username;
    
    @XmlElement(name="password")
    private String password;

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}

