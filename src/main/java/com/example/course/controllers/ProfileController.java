package com.example.course.controllers;

import com.example.course.helpers.RegisterHelper;
import com.example.course.models.Bonus;
import com.example.course.models.Company;
import com.example.course.models.User;
import com.example.course.service.BonusService;
import com.example.course.service.CompanyService;
import com.example.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;
import java.util.Set;


@Controller
public class ProfileController
{
    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private BonusService bonusService;

    @GetMapping("/profile/data")
    public String getProfile(Model model, Principal principal)
    {
        model.addAttribute("user", userService.findByUsername(principal.getName()));

        return "profile_data";
    }

    @PostMapping("/profile/data")
    public String changeProfile(@ModelAttribute User changedUser, Principal principal)
    {
        RegisterHelper registerHelper = new RegisterHelper();

        User user = userService.findByUsername(principal.getName());

        user.setFirstname(changedUser.getFirstname());
        user.setLastname(changedUser.getLastname());
        user.setEmail(changedUser.getEmail());

        user.setPassword(registerHelper.passwordEncoder(changedUser));

        userService.save(user);

        return "redirect:/";
    }


    @GetMapping("/profile/companies")
    public String getCompanies(Principal principal, Model model)
    {
        User user = userService.findByUsername(principal.getName());
        List<Company> companies;

        if (userService.isHasRole(user, "ADMIN"))
            companies = companyService.findAll();
        else
            companies = companyService.findByUserId(user.getId());

        model.addAttribute("count", companies.size());
        model.addAttribute("companies", companies);

        return "companies";
    }

    @GetMapping("/profile/create/company")
    public String newCompanyForm(Model model)
    {
        model.addAttribute("company", new Company());

        return "profile_create_company";
    }

    @PostMapping("/profile/create/company")
    public String createCompany(@ModelAttribute Company company, Principal principal)
    {
        User user = userService.findByUsername(principal.getName());

        company.setUser(user);

        company.setCurrentSum(0.0f);

        companyService.save(company);

        return "redirect:/companies";
    }

    @GetMapping("/profile/bonuses")
    public String getBonuses(Model model, Principal principal)
    {
        User user = userService.findByUsername(principal.getName());
        Set<Bonus> bonuses = bonusService.findByUser(user);

        model.addAttribute("bonusCount", bonuses.size());
        model.addAttribute("bonuses", bonuses);

        return "profile_bonuses";
    }

    @GetMapping("profile/create/bonus")
    public String newBonusForm(@ModelAttribute("companyId") Long companyId, Model model, Principal principal)
    {
        model.addAttribute("bonus", new Bonus());
        model.addAttribute("companyId", companyId);

        return "profile_create_bonus";
    }

    @PostMapping("profile/create/bonus")
    public String createBonus(@ModelAttribute Bonus bonus, @ModelAttribute("companyId") Long companyId,
                              RedirectAttributes rattrs)
    {
        Company company = companyService.findById(companyId);

        bonus.setCompany(company);

        bonusService.save(bonus);

        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @GetMapping("profile/edit/company")
    public String editCompanyForm(@ModelAttribute("companyId") Long companyId, Model model)
    {
        Company company = companyService.findById(companyId);

        model.addAttribute("companyId", companyId);
        model.addAttribute("company", company);

        return "profile_edit_company";
    }

    @PostMapping("profile/edit/company")
    public String changeCompany(@ModelAttribute Company company, @ModelAttribute("companyId") Long companyId,
                                RedirectAttributes rattrs)
    {
        Company changedCompany = companyService.findById(companyId);

        changedCompany.setTopic(company.getTopic());
        changedCompany.setTags(company.getTags());
        changedCompany.setDescription(company.getDescription());
        changedCompany.setYoutubeURL(company.getYoutubeURL());
        changedCompany.setCompanyGoal(company.getCompanyGoal());
        changedCompany.setExpirationDate(company.getExpirationDate());

        companyService.save(changedCompany);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @GetMapping("profile/delete/company")
    public String deleteCompany(@ModelAttribute("companyId") Long companyId, Model model)
    {
        model.addAttribute("companyId", companyId);

        return "profile_delete_company_confirm";
    }

    @RequestMapping(value = "/profile/delete/company/confirm", method = RequestMethod.POST, params = "cancel")
    public String cancelDeleteCompany(@ModelAttribute("companyId") Long companyId, Model model)
    {
        model.addAttribute("companyId", companyId);

        return "redirect:/companies/detail?companyId=" + companyId;
    }

    @RequestMapping(value = "/profile/delete/company/confirm", method = RequestMethod.POST, params = "confirm")
    public String confirmDeleteCompany(@ModelAttribute("companyId") Long companyId)
    {
        companyService.deleteById(companyId);

        return "redirect:/companies";
    }
}
