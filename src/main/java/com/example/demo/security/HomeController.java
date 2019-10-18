package com.example.demo.security;

import com.example.demo.Job;
import com.example.demo.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

@Controller
public class HomeController {
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired JobRepository jobRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        if(result.hasErrors())
            return "registration";
        else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "list";
    }
    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("jobs", jobRepository.findAll());
        if(userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "list";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }
//    @RequestMapping("/admin")
//    public String admin(){
//        return "admin";
//    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    /*
    CraigsList Stuff
     */

    @GetMapping("/add")
    public String jobForm(Model model){
        model.addAttribute("job", new Job());
        return "jobform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute Job job, @RequestParam(name="postedDate") String postedDate){

        // Used this because date was randomly giving a ','; will keep if it shows up again,
        // I realize that formattedDate is the same as date...
        try {
            String pattern = "yyyy-MM-dd";
//            System.out.println(postedDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String formattedDate = postedDate.substring(0);
//            System.out.println("formatted: " + formattedDate);
            Date realDate = simpleDateFormat.parse(formattedDate);
//            System.out.println(realDate.toString());
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
        }
        job.setUser(userService.getUser());
        jobRepository.save(job);
        return "redirect:/";
    }

    @PostMapping("/searchlist")
    public String searchResult(Model model, @RequestParam(name="search") String search) {
        String[] split = search.split(" ");
        HashMap<String, TreeSet<Job>> mapToAdd = new HashMap<>();
        mapToAdd.put("jobs", new TreeSet<Job>());
        for(String s : split)
            for(Job j : jobRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(s,s))
                mapToAdd.get("jobs").add(j);

        model.addAllAttributes(mapToAdd);
        return "searchlist";
    }

    @RequestMapping("/update/{id}")
    public String updateFlight(@PathVariable("id") long id , Model model){
        model.addAttribute("job", jobRepository.findById(id).get());
        return "jobform";
    }

    @RequestMapping("/detail/{id}")
    public String showFlight(@PathVariable("id") long id, Model model){
        model.addAttribute("job", jobRepository.findById(id).get());
        return "jobdetail";
    }

    @RequestMapping("/delete/{id}")
    public String delFlight(@PathVariable("id") long id) {
        jobRepository.deleteById(id);
        return "redirect:/";
    }
}
