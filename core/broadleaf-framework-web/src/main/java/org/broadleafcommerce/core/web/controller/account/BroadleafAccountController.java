/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.controller.account;

import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The controller responsible for the view my account page, update profile,
 * and change password.
 * 
 * @author bpolster
 */
public class BroadleafAccountController extends AbstractAccountController {
	private String myAccountView = "/account/myaccount";
	
	/*
	 * TODO: UpdateProfileDTO
	 * TODO: ChangePasswordDTO

	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String profile(HttpServletRequest request, HttpServletResponse response, Model model) {
		return super.profile(request, response, model);
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST)
	public String updateProfile(HttpServletRequest request, HttpServletResponse response, Model model) {
		return super.updateProfile(request, response, model);
	}		
	
	@RequestMapping(value="/changePassword", method=RequestMethod.GET)
	public String changePassword(HttpServletRequest request, HttpServletResponse response, Model model) {
		return super.changePassword(request, response, model);
	}
	
	@RequestMapping(value="/changePassword", method=RequestMethod.POST)
    public String processChangePassword(@RequestParam("emailAddress") String emailAddress, HttpServletRequest request, Model model) {
    	return super.processChangePassword(emailAddress, request, model);
    } 
    
    */		
	
	public String myaccount(HttpServletRequest request, HttpServletResponse response, Model model) {
		return myAccountView;		
	}

	public String getMyAccountView() {
		return myAccountView;
	}

	public void setMyAccountView(String myAccountView) {
		this.myAccountView = myAccountView;
	}		
}
