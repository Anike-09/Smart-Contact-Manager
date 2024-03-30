package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.catalina.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {

	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailservice;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; 
	
	
	
	//email id form open handler
	
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	//
	@PostMapping("/send-otp")
	public String SendOtp(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("email-Id"+email);
		//Generating Otp Of 4 Digit
		
		
		int otp = random.nextInt(999999);
		System.out.println(" Your OTP is ->"+otp);
		
		//write code for send otp to emil
	
		String subject="OTP FROM SCM";
		
		String message = " Your OTP :"+ otp+""; 
		
		String to=email;
		
		boolean flag = this.emailservice.sendEmail(subject, message, to);
		
		System.out.println(flag);
		
		if(!flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
			
		}
		else
		{
			session.setAttribute("message","Check Your Email id!!");
			return "forgot_email_form";
		}
		
		
		
	}
	//varify Otp
	@PostMapping("/varify-otp")
	public  String varifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		Integer myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		
		if(myOtp==otp)
		{
			//password Change Form
			
		   User user = this.userRepository.getUserByUserName(email);
		   
		   if(user==null)
		   {
			   //send error message 
			   session.setAttribute("message","User Does Not Exist with This email!!");
				return "forgot_email_form";
		   }
		   else
		   {
			   
		   }
			
			return "password_change_form";
		}
		else
		{
			session.setAttribute("message", "you have Entered Wrong Otp");
			return "verify_otp";
		}
		
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword, HttpSession session)
	{
		String email=(String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		
		return"redirect:/signin?change=password changed successfully";
		
		
	}	
		
}
