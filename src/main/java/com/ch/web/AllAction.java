package com.ch.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ch.service.DatatoJson;

@Controller
@RequestMapping("/All")
public class AllAction {
	@Autowired
	DatatoJson datatoJson;
	
	@RequestMapping("/datatojson")
	public ModelAndView datatojson(){
		return new ModelAndView("/testjsp/datatojson");
	}
	
	@RequestMapping("/datatojsontest")
	public ModelAndView datatojsontest(HttpServletRequest req){
		ModelAndView mav=new ModelAndView("/testjsp/datatojson");
		if(req.getParameter("visitcode")==null){
			return mav;
		}
		mav.addObject("visitcode", req.getParameter("visitcode").toString());
		mav.addObject("result", datatoJson.pa_winresult(req.getParameter("visitcode").toString()));
		return mav;	
	}
}
