package com.example.course.controllers;

import com.example.course.helpers.DonateHelper;
import com.example.course.helpers.RegisterHelper;
import com.example.course.models.*;
import com.example.course.service.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;;
import java.security.Principal;
import java.util.List;
import java.util.Set;


@Controller
public class HomeController
{
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
    public String home()
    {
        return "redirect:/companies";
    }

    @GetMapping("/about")
    public String about()
    {
        return "about";
    }

    @GetMapping("/companies")
    public String getCompanies(@ModelAttribute("topic") String topic, Model model)
    {
        List<Company> companies;

        if (topic.equals(""))
            companies = companyService.findAll();
        else
            companies = companyService.findByTopic(topic);

        model.addAttribute("count", companies.size());
        model.addAttribute("companies", companies);

        return "companies";
    }

    @GetMapping("/companies/search")
    public String searchCompanies(@RequestParam(value = "searchText", required = false) String searchText,
                                  Model model) throws InterruptedException
    {
        List<Company> companies;

        if (searchText.equals(""))
            companies = companyService.findAll();

        else
        {
            indexingService.initiateIndexing();
            companies = searchService.getCompanyBasedOnWord(searchText);
        }

        model.addAttribute("count", companies.size());
        model.addAttribute("companies", companies);

        return "companies";
    }

    @GetMapping("/companies/detail")
    public String companyDetail(@ModelAttribute("companyId") Long companyId,
                                Principal principal, Model model)
    {
        Long userId;
        List<Comment> comments = commentService.findByCompanyId(companyId);
        List<News> news = newsService.findByCompanyId(companyId);

        float raiting;

        if (principal != null)
            userId = userService.findByUsername(principal.getName()).getId();
        else
            userId = null;

        Company company = companyService.findById(companyId);

        if (company == null)
            return "redirect:/404";

        boolean isOwner = company.getUser().getId().equals(userId);

        if (company.getRateCount() > 0)
            raiting = DonateHelper.round((float)company.getTotalRate() / company.getRateCount(), 2);
        else
            raiting = 0;

        model.addAttribute("raiting", raiting);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("newNews", new News());
        model.addAttribute("news", news);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("company", company);

        return "companies_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/addComment")
    public String companyAddComment(@ModelAttribute("companyId") Long companyId, @ModelAttribute Comment comment,
                                        Principal principal, RedirectAttributes rattrs)
    {
        Company company = companyService.findById(companyId);
        User user = userService.findByUsername(principal.getName());

        comment.setCompany(company);
        comment.setUser(user);
        comment.setDate(RegisterHelper.getCurrentDateTime());

        commentService.save(comment);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "companies/detail/addRaiting", method = RequestMethod.POST)
    public String companyAddRaiting(@ModelAttribute("companyId") Long companyId,
                                    @RequestParam(value = "idChecked", required = false) String rateValue,
                                    Principal principal, RedirectAttributes rattrs)
    {
        Company company = companyService.findById(companyId);
        User user = userService.findByUsername(principal.getName());

        if (!raitingService.isExistByUserId(user.getId()))
        {
            Raiting raiting = new Raiting(Integer.parseInt(rateValue), company, user.getId());

            raitingService.save(raiting);

            company.setTotalRate(company.getTotalRate() + Integer.parseInt(rateValue));
            company.setRateCount(company.getRateCount() + 1);

            companyService.save(company);

        }
        rattrs.addAttribute("companyId", companyId);
        return "redirect:/companies/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/addNews")
    public String companyAddNews(@ModelAttribute("companyId") Long companyId, @ModelAttribute News news,
                                 RedirectAttributes rattrs)
    {
        Company company = companyService.findById(companyId);

        news.setCompany(company);
        news.setDate(RegisterHelper.getCurrentDateTime());

        newsService.save(news);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("companies/detail/support")
    public String companySupportForm(@ModelAttribute("companyId") Long companyId, Model model)
    {
        List<Bonus> bonuses = bonusService.findByCompanyId(companyId);
        Company company = companyService.findById(companyId);

        model.addAttribute("bonusCount", bonuses.size());
        model.addAttribute("bonuses", bonuses);
        model.addAttribute("company", company);

        return "companies_detail_support";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("companies/detail/support/donate")
    public String donateCompany(@ModelAttribute("companyId") Long companyId,
                                @RequestParam(value = "donateSum", required = false) String donateSum,
                                RedirectAttributes rattrs)
    {
        Company company = companyService.findById(companyId);

        float sum = company.getCurrentSum() + Float.parseFloat(donateSum);

        company.setCurrentSum(sum);

        companyService.save(company);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("companies/detail/support/buy")
    public String buyBonus(@ModelAttribute("companyId") Long companyId, @ModelAttribute("bonusId") Long bonusId,
                           Principal principal, RedirectAttributes rattrs)
    {
        Company company = companyService.findById(companyId);
        Bonus bonus = bonusService.findById(bonusId);
        User user = userService.findByUsername(principal.getName());

        if (!bonusService.isExistByUser(user, bonusId))
        {
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
    public String logout()
    {
        return "logout";
    }
}
