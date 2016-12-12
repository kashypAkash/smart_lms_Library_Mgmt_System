package com.lms.cmpe.controller;


import com.lms.cmpe.model.ApplicationTime;
import com.lms.cmpe.model.DataObj;
import com.lms.cmpe.model.User;
import com.lms.cmpe.service.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Controller
public class HelloController {

    @Autowired
    private  SessionFactory sessionFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ApplicationTime applicationTime;

    @GetMapping("/mail")
    public String test(){

            SimpleMailMessage mailMessage=new SimpleMailMessage();
            mailMessage.setTo("senjusforest@gmail.com");
            mailMessage.setSubject("Registration");
            mailMessage.setText("Hello ");
            javaMailSender.send(mailMessage);
        return "home";

    }

    @PostMapping("/add")
    public String check(@RequestParam(value = "firstname") String firstname,
                      @RequestParam(value = "lastname") String lastname,
                      @RequestParam(value = "title") String title,
                      @RequestParam(value = "street") String street,
                      @RequestParam(value = "city") String city,
                      @RequestParam(value = "state") String state,
                      @RequestParam(value = "zip") String zip,
                      @RequestParam(value = "phones") String phones
                      ){

        User user = new User();
       /* List<Phone> phoneList = userService.getUsersByNumber(phones);
        if(phoneList!=null){
            User user = new User(firstname,lastname,title,new Address(street,city,state,zip),phoneList);
            userService.saveUser(user);
            return "redirect:/users";
        }*/
            return "badrequest";

    }

    @RequestMapping("/user/{id}")
    public String getUserById(Model model,@PathVariable("id") int id){
        User user = new User();
        /*User user = userService.getUserById(id);
        model.addAttribute("user",user);
        model.addAttribute("userid",user.getUserId());
        model.addAttribute("addressid",user.getAddress().getAddressId());*/
        return "user";
    }

    @RequestMapping(value = "/user/{id}", params = "json")
    @ResponseBody
    public User getJosn(Model model,@PathVariable("id") int id){
        User user = userService.getUserById(id);
        return user;
    }

    @RequestMapping(value = "/getDate",method = RequestMethod.POST)
    public String getDate(@RequestParam("dateValue") String dateInString, RedirectAttributes redirectAttributes,HttpSession session)
    {


        if(applicationTime.setAppDateTime(dateInString)){
            redirectAttributes.addFlashAttribute("message",dateInString);
            Date appTime;
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
            try {
                appTime = formatter.parse(dateInString);
                session.setAttribute("appTime", appTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            return "redirect:/profile";
        }
        else{
            redirectAttributes.addFlashAttribute("message","invalid date format");
            return "redirect:/myerror";
        }
        //ApplicationTime app = new ApplicationTime(appDateTime);


        //return "redirect:myerror"
    }

    public String parseDate(String input, Map<String,String> map)
    {
        String finalText = "";
        String text = input.substring(input.indexOf(' ')+1);
        text = text.substring(0,text.indexOf(' '));
        String[] array = input.split("\\s+");
        for(String key : map.keySet())
        {
            if(key.equals(text))
            {
                text = map.get(key);
                break;
            }
        }
        finalText = array[2]+"/"+text+"/"+array[3]+" "+array[4];
        return finalText;
    }
    @RequestMapping(value = "/updateBook",method = RequestMethod.POST)
    public String modify(@ModelAttribute User user, @RequestParam(value="action", required=true) String action,
                                                    @RequestParam(value="id", required=true) int id,
                                                    @RequestParam(value="addressid", required=true) int addressId){

       /* user.setUserId(id);
        user.getAddress().setAddressId(addressId);

        if(action.equals("update")){

            userService.updateUser(user);
        }

        if(action.equals("delete")){
            userService.deleteUser(user);
        }*/
        return String.format("redirect:/user/%d",user.getUserId());
    }

    @RequestMapping("/users")
    public String users(Model modelMap){
        modelMap.addAttribute("users", userService.getUsers());
        return "home";
    }

}
