package com.app.nouapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.nouapp.dto.AdminInfoDto;
import com.app.nouapp.dto.EnquiryDto;
import com.app.nouapp.dto.StudentInfoDto;

import com.app.nouapp.model.AdminInfo;
import com.app.nouapp.model.Enquiry;
import com.app.nouapp.model.StudentInfo;
import com.app.nouapp.service.AdminInfoRepository;
import com.app.nouapp.service.EnquiryRepository;
import com.app.nouapp.service.StudentInfoRepository;

import jakarta.servlet.http.HttpSession;



@Controller
public class MainController {
	@Autowired
	StudentInfoRepository srepo;
	
	@Autowired
	EnquiryRepository erepo;
	
	@Autowired
	AdminInfoRepository adrepo;
	
	
	@GetMapping
	public String showIndex() {
		return "index";
		
	}
	
	
	@GetMapping("/aboutus")
	public String showAboutUs() {
		return "aboutus";
	}
	
	@GetMapping("/registration")
	public String showRegistration(Model model) {
		StudentInfoDto dto= new StudentInfoDto();
		model.addAttribute("dto",dto);
		return "registration";
	}
	@PostMapping("/registration")
	public String Registration(@ModelAttribute StudentInfoDto dto,RedirectAttributes attrib) 
	{
		try {
		StudentInfo stdinfo = new StudentInfo();
		stdinfo.setEnrollmentno(dto.getEnrollmentno());
		stdinfo.setName(dto.getName());
		stdinfo.setFname(dto.getFname());
		stdinfo.setMname(dto.getMname());
		stdinfo.setGender(dto.getGender());
		stdinfo.setAddress(dto.getAddress());
		stdinfo.setProgram(dto.getProgram());
		stdinfo.setBranch(dto.getBranch());
		stdinfo.setYear(dto.getYear());
		stdinfo.setContactno(dto.getContactno());
		stdinfo.setEmailaddress(dto.getEmailaddress());
		stdinfo.setPassword(dto.getPassword());
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
		String regdate=df.format(dt);
		stdinfo.setRegdate(regdate);
		srepo.save(stdinfo);
		attrib.addFlashAttribute("msg" , "Registration successful!");
		return "redirect:/registration";
	
		} 
		catch(Exception e) {
			attrib.addFlashAttribute("msg","Something went wrong"+e.getMessage());
			return "redirect:/registration";
		
		}
		
	}
	
	@GetMapping("/stulogin")
	public String showStudentLogin(Model model) 
	{ 
		StudentInfoDto dto=new StudentInfoDto();
	    model.addAttribute("dto", dto);
	    return "stulogin";
	}
	
	@PostMapping("/stulogin")
	public String StudentLogin(@ModelAttribute StudentInfoDto dto, HttpSession session, RedirectAttributes attributes) {
		try {
		StudentInfo si=srepo.getById(dto.getEnrollmentno());
		if(si.getPassword().equals(dto.getPassword())) 
		{
			session.setAttribute("studentid", dto.getEnrollmentno());
			return "redirect:/student/stdhome";
		}
		else {
			attributes.addFlashAttribute("msg", "invalid user");
			return "redirect:/stulogin";
			
		}
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", "user does not exist");
			return "redirect:/stulogin";
		}
	}
	
	
	@GetMapping("/adminlogin")
	public String showAdminlogin(Model model) 
	{   AdminInfoDto dto=new AdminInfoDto();
	    model.addAttribute("dto",dto);
		return "adminlogin";
	}
	@PostMapping("/adminlogin")
	public String AdminL(@ModelAttribute AdminInfoDto dto, HttpSession session, RedirectAttributes attributes) {
		
		try {
			
			AdminInfo ad = adrepo.getById(dto.getUserid());
			if(ad.getPassword().equals(dto.getPassword()))
			{
				//attributes.addFlashAttribute("msg","valid user");
				session.setAttribute("admin", ad.getUserid().toString());
				return "redirect:/admin/adhome";
			}
			else
			{
				attributes.addFlashAttribute("msg","valid user");
				return "redirect:/adminlogin";
			}
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg","User does not exist" +e.getMessage());
			return "redirect:/adminlogin";
		}
	}
	
	
	@GetMapping("/contactus")
	public String showContactUs(Model model) {
		EnquiryDto dto = new EnquiryDto();
		model.addAttribute("dto",dto);
		return "contactus";
	}
	@PostMapping("/contactus")
	public String saveEnquiry(@ModelAttribute EnquiryDto dto,RedirectAttributes attrib)
	{
		Enquiry e=new Enquiry();
		e.setName(dto.getName());
		e.setGender(dto.getGender());
		e.setAddress(dto.getAddress());
		e.setContactno(dto.getContactno());
		e.setEmailaddress(dto.getEmailaddress());
		e.setEnquirytext(dto.getEnquirytext());
		Date dt=new Date();
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		String posteddate = df.format(dt);
		e.setPosteddate(posteddate);
		erepo.save(e);
		attrib.addFlashAttribute("msg", "Enquiry is saved");
		return "redirect:/contactus" ;
	}
	
	

}
