package com.app.nouapp.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.nouapp.dto.ResponseDto;
import com.app.nouapp.model.AdminInfo;
import com.app.nouapp.model.Response;
import com.app.nouapp.model.StudentInfo;
import com.app.nouapp.model.StudyMaterial;
import com.app.nouapp.service.ResponseRepository;
import com.app.nouapp.service.StudentInfoRepository;
import com.app.nouapp.service.StudyMaterialRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {
	@Autowired
	StudentInfoRepository stdrepo;
	@Autowired
	ResponseRepository resrepo;
	@Autowired
	StudyMaterialRepository smrepo;
	
	@GetMapping("/stdhome")
	public String ShowStudentDashboard(Model model , HttpSession session) {
		if(session.getAttribute("studentid")!=null)
		{
			StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
			model.addAttribute("stdinfo", stdinfo);
			 model.addAttribute("name", stdinfo.getName());
			return "student/studentdashboard";
		}
		else {
			return "redirect:/stulogin";
		}
		
	}
	@PostMapping("stdhome")
	public String UploadPic(@RequestParam MultipartFile file, RedirectAttributes attributes, HttpSession session) {
		try {
			String storageFileName=file.getOriginalFilename();
			String uploadDir="public/profile/";
			Path uploadPath=Paths.get(uploadDir);
			if(!Files.exists(uploadPath)){
				Files.createDirectories(uploadPath);
			}
			try(InputStream inputStream=file.getInputStream()){
				Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			StudentInfo std=stdrepo.findById((long)session.getAttribute("studentid")).get();
			std.setProfilepic(storageFileName);
			stdrepo.save(std);
			attributes.addFlashAttribute("msg", "profile pic uploaded successfully");
			return "redirect:/student/stdhome";
		} catch (Exception e) {
			attributes.addFlashAttribute("msg", "Something went wrong"+e.getMessage());
			return "redirect:/student/stdhome";
		}
	}
	
	@GetMapping("/studymaterial")
	public String ShowStudyMaterial(Model model , HttpSession session) {
		if(session.getAttribute("studentid")!=null) {
			StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
			 model.addAttribute("name", stdinfo.getName());
			String program=stdinfo.getProgram();
			String branch=stdinfo.getBranch();
			String year=stdinfo.getYear();
			String materialtype= "studymaterial";
			List<StudyMaterial> smlist=smrepo.findAllbyType(program,branch,year,materialtype);
			model.addAttribute("smlist", smlist);
			return "student/studymaterial";
		}
		else {
			return "redirect:/stulogin";
		}
		
	}
	
	
	
	@GetMapping("/assignment")
	public String ShowAssignment(Model model , HttpSession session) {
		if(session.getAttribute("studentid")!=null) {
			StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
			 model.addAttribute("name", stdinfo.getName());
			 String program=stdinfo.getProgram();
				String branch=stdinfo.getBranch();
				String year=stdinfo.getYear();
				String materialtype= "assignment";
				List<StudyMaterial> smlist=smrepo.findAllbyType(program,branch,year,materialtype);
				model.addAttribute("smlist", smlist);
				
			return "student/assignment";
		}
		else {
			return "redirect:/stulogin";
		}
		
	}
	@GetMapping("/giveresponse")
	public String ShowGiveResponse(Model model , HttpSession session) {
		if(session.getAttribute("studentid")!=null)
		{
			StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
			 model.addAttribute("name", stdinfo.getName());
			
			ResponseDto dto = new ResponseDto();
			model.addAttribute("dto", dto);
			return "student/giveresponse";
		}
		else {
			return "redirect:/stulogin";
		}
		
	}
	@PostMapping("/giveresponse")
	public String SubmitResponse(@ModelAttribute ResponseDto dto, HttpSession session,RedirectAttributes attributes) 
	{
		if(session.getAttribute("studentid")!=null)
		{
			try {
				StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
				
				Response res=new Response();
				res.setName(stdinfo.getName());
				res.setEnrollmentno(stdinfo.getEnrollmentno());
				res.setContactno(stdinfo.getContactno());
				res.setResponsetype(dto.getResponsetype());
				res.setResponsetitle(dto.getResponsetitle());
				res.setResponsetext(dto.getResponsetext());
				Date dt=new Date();
				SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
				String resdate=df.format(dt);
				res.setResdate(resdate);
				resrepo.save(res);
				attributes.addFlashAttribute("ms", "Response submitted successfully");
				return "redirect:/student/giveresponse";
						}
			catch (Exception e) {
				attributes.addFlashAttribute("msg", "Something went wrong" +e.getMessage());
				return "redirect:/student/giveresponse";
			}
		}
		else {
			return "redirect:/stulogin";
		}
	}
	@GetMapping("/changepassword")
	public String ShowChangePassword(Model model , HttpSession session) {
		if(session.getAttribute("studentid")!=null) {
			StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
			 model.addAttribute("name", stdinfo.getName());
			
			return "student/changepassword";
		}
		else {
			return "redirect:/stulogin";
		}
		
	}
	
	@PostMapping("/changepassword")
	public String ChangePassword(HttpSession session,RedirectAttributes attributes, HttpServletRequest request)
	{
		try {
			StudentInfo stdinfo=stdrepo.getById((long)session.getAttribute("studentid"));
			String oldpass=request.getParameter("oldpass");
			String newpass=request.getParameter("newpass");
			String confirmpass=request.getParameter("confirmpass");
			if(newpass.equals(confirmpass))
			{
				if(oldpass.equals(stdinfo.getPassword())) {
					stdinfo.setPassword(confirmpass);
					stdrepo.save(stdinfo);
					session.invalidate();
					attributes.addFlashAttribute("msg", "Password change successfully...");
					return "redirect:/stulogin";
				}else {
					attributes.addFlashAttribute("message", "Invalid old password");
				}
				
			}else {
				attributes.addFlashAttribute("message", "new password and confirm password not match");
			}
			
			return "redirect:/student/changepassword";
		} catch (Exception e) {
			attributes.addFlashAttribute("message", "Something went wrong" +e.getMessage());
			return "redirect:/student/changepassword";
		}
	}
	
	
	@GetMapping("/logout")
	public String Logout(Model model , HttpSession session) {
		if(session.getAttribute("studentid")!=null) {
			session.invalidate();
			return "redirect:/student/stdhome";
		}
		else {
			return "redirect:/stulogin";
		}
		
	}




}
