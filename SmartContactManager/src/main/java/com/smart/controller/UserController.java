package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principle) {
		String UserName = principle.getName();
		System.out.println("User Name:" + UserName);
		User user = userRepository.getUserByUserName(UserName);

		System.out.println("user:" + user);

		model.addAttribute("user", user);
	}

	// dashboard Home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principle) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing Add contact
	@PostMapping("process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principle, HttpSession session) {
		try {
			String name = principle.getName();
			User user = this.userRepository.getUserByUserName(name);

			if (file.isEmpty()) {
				System.out.println("File empty");
				contact.setImage("CONTACT.png");

			} else {
				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image Uploaded");
			}
			contact.setUser(user);
			user.getContact().add(contact);
			this.userRepository.save(user);
			System.out.println("Data" + contact);
			System.out.println("Added To Database");

			// message Success
			session.setAttribute("message", new Message("Your Contact is Added!! New More", "success"));

		} catch (Exception e) {

			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
			// error Message
			session.setAttribute("message", new Message("Something went Wrong!!! Try Again", "danger"));

		}
		return "normal/add_contact_form";

	}

	// show contact handler
	// per page 5 cotnact
	@GetMapping("/show_contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principle) {
		String userName = principle.getName();

		User user = this.userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 4);

		Page<Contact> contacts = this.contactRepository.findContactsyUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";

	}

	// showing perticular contact Details...

	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principle) {
		System.out.println(cid);
		Optional<Contact> contactoptional = this.contactRepository.findById(cid);
		Contact contact = contactoptional.get();

		//
		String userName = principle.getName();
		User User = this.userRepository.getUserByUserName(userName);

		if (User.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "/normal/contact_detail";
	}

	// delete Contact Handler.
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principle) {
		Contact contact = this.contactRepository.findById(cId).get();

		User user = this.userRepository.getUserByUserName(principle.getName());

		user.getContact().remove(contact);

		this.userRepository.save(user);

		session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
		return "redirect:/user/show_contacts/0";
	}

	// Update form
	@PostMapping("/update-contact/{cid}")
	public String ipdateForm(@PathVariable("cid") Integer cid, Model m) {

		m.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cid).get();

		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principle) {
		try {

			Contact oldcontactDetails = this.contactRepository.findById(contact.getCid()).get();

			if (!file.isEmpty()) {

				File savefile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldcontactDetails.getImage());
			}
			User user = this.userRepository.getUserByUserName(principle.getName());

			contact.setUser(user);

			this.contactRepository.save(contact);

			session.setAttribute("message", new Message("Your Contact Updated Successfully", "success"));

		} catch (Exception e) {

		}

		System.out.println("Contact Name" + contact.getName());
		System.out.println("Contact Id" + contact.getCid());
		return "redirect:/user/" + contact.getCid() + "/contact";

	}

	// Your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	// open setting handler
	@GetMapping("/settings")
	public String openSettion() {
		return "normal/settings";
	}

	// change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		System.out.println("old password" + oldPassword);
		System.out.println("old password" + newPassword);

		String UserName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(UserName);

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your Password is Successfully Changed", "success"));

		} else {
			session.setAttribute("message", new Message("Please Enter Correct old Correct Password ", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}

}