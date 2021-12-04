package com.example.course.controllers;

import com.example.course.helpers.DonateHelper;
import com.example.course.models.*;
import com.example.course.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import static com.example.course.helpers.DonateHelper.round;
import static com.example.course.helpers.RegisterHelper.getCurrentDateTime;


@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BonusService bonusService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private RaitingService raitingService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private IndexingService indexingService;

    @GetMapping("/")
    public String home() {
        return "redirect:/companies";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/companies")
    public String getCompanies(@ModelAttribute("topic") String topic, Model model) {
        List<Company> companies;

        if (topic.equals("")) {
            companies = companyService.findAll();
        } else {
            companies = companyService.findByTopic(topic);
        }
        model.addAttribute("companies", companies);
        return "companies";
    }

    @GetMapping("/companies/search")
    public String searchCompanies(@RequestParam(value = "searchText", required = false) String searchText,
                                  Model model) throws InterruptedException {
        List<Company> companies;
        if (searchText.equals(""))
            companies = companyService.findAll();

        else {
            indexingService.initiateIndexing();
            companies = searchService.getCompanyBasedOnWord(searchText);
        }
        model.addAttribute("companies", companies);
        return "companies";
    }

    @GetMapping("/companies/detail")
    public String companyDetail(@ModelAttribute("companyId") Long companyId, Model model,
                                Principal principal) {
        float averageRating = 0;
        float userRateValue = 0;
        boolean isOwner = false;

        Company company = companyService.findById(companyId);
        if (company == null)
            return "redirect:/404";

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            isOwner = company.getUser().getId().equals(user.getId());
            if (raitingService.findByCompanyIdAndUserId(companyId, user.getId()) != null) {
                userRateValue = raitingService.findByCompanyIdAndUserId(companyId, user.getId()).getValue();
            }
        }

        if (company.getRateCount() > 0) {
            averageRating = round((float) company.getTotalRate() / company.getRateCount(), 2);
        }

        model.addAttribute("userRateValue", userRateValue);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("newNews", new News());
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("company", company);

        return "companies_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/addComment")
    public String companyAddComment(@ModelAttribute("companyId") Long companyId, @ModelAttribute Comment comment,
                                    RedirectAttributes rattrs, Principal principal) {

        Company company = companyService.findById(companyId);
        User user = userService.findByUsername(principal.getName());

        comment.setCompany(company);
        comment.setUser(user);
        comment.setDate(getCurrentDateTime());

        commentService.save(comment);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/addRaiting")
    public String companyAddRaiting(@ModelAttribute("companyId") Long companyId,
                                    @RequestParam(value = "idChecked", required = false) String rateValue,
                                    RedirectAttributes rattrs, Principal principal) {

        Company company = companyService.findById(companyId);
        User currUser = userService.findByUsername(principal.getName());
        Raiting raiting;
        rateValue = rateValue.substring(0, 1);

        if (!raitingService.isExistByCompanyIdAndUserId(companyId, currUser.getId())) {
            raiting = new Raiting(Integer.parseInt(rateValue), company, currUser);
            company.setTotalRate(company.getTotalRate() + Integer.parseInt(rateValue));
            company.setRateCount(company.getRateCount() + 1);
        } else {
            raiting = raitingService.findByCompanyIdAndUserId(companyId, currUser.getId());
            company.setTotalRate(company.getTotalRate() - raiting.getValue() + Integer.parseInt(rateValue));
            raiting.setValue(Integer.parseInt(rateValue));
        }

        raitingService.save(raiting);
        companyService.save(company);

        rattrs.addAttribute("companyId", companyId);
        return "redirect:/companies/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/addNews")
    public String companyAddNews(@ModelAttribute("companyId") Long companyId, @ModelAttribute News news,
                                 RedirectAttributes rattrs) {
        Company company = companyService.findById(companyId);

        news.setCompany(company);
        news.setDate(getCurrentDateTime());

        newsService.save(news);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("companies/detail/support")
    public String companySupportForm(@ModelAttribute("companyId") Long companyId,
                                     @ModelAttribute("isSumWrong") String isSumWrong, Model model) {
        List<Bonus> bonuses = bonusService.findByCompanyId(companyId);
        Company company = companyService.findById(companyId);

        model.addAttribute("bonuses", bonuses);
        model.addAttribute("company", company);

        return "companies_detail_support";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/support/donate")
    public String donateCompany(@ModelAttribute("companyId") Long companyId,
                                @RequestParam(value = "donateSum", required = false) String donateSum,
                                RedirectAttributes rattrs) {
        Company company = companyService.findById(companyId);
        rattrs.addAttribute("companyId", companyId);

        if (Float.parseFloat(donateSum) <= 0f) {
            rattrs.addAttribute("isSumWrong", "1");
            return "redirect:/companies/detail/support";
        }
        float sum = company.getCurrentSum() + Float.parseFloat(donateSum);

        company.setCurrentSum(round(sum, 2));
        companyService.save(company);

        return "redirect:/companies/detail";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("companies/detail/support/buy")
    public String buyBonus(@ModelAttribute("companyId") Long companyId, @ModelAttribute("bonusId") Long bonusId,
                           RedirectAttributes rattrs, Principal principal) {
        Company company = companyService.findById(companyId);
        Bonus bonus = bonusService.findById(bonusId);
        User user = userService.findByUsername(principal.getName());

        if (!bonusService.isExistByUser(user, bonusId)) {
            float sum = company.getCurrentSum() + bonus.getPrice();

            company.setCurrentSum(sum);
            companyService.save(company);
            Set<User> users = bonus.getUsers();
            users.add(userService.findByUsername(principal.getName()));
            bonus.setUsers(users);
            bonusService.save(bonus);
        }

        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
