package com.example.course.controllers;

import com.example.course.helpers.RegisterHelper;
import com.example.course.models.Bonus;
import com.example.course.models.Company;
import com.example.course.models.User;
import com.example.course.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.course.helpers.RegisterHelper.passwordEncoder;
import static com.example.course.helpers.UrlHelper.*;


@PreAuthorize("isAuthenticated()")
@Controller
public class ProfileController {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private BonusService bonusService;

    @GetMapping("/profile/data")
    public String getProfile(Model model, Principal principal,
                             @ModelAttribute("duplicateField") String duplicateField) {
        User user = userService.findByUsername(principal.getName());
        boolean isVerified = secureTokenService.findByUserId(user.getId()) == null;
        model.addAttribute("user", userService.findByUsername(principal.getName()));
        model.addAttribute("isVerified", isVerified);
        return "profile_data";
    }

    @PostMapping("/profile/data")
    public String changeProfile(@ModelAttribute User changedUser, Principal principal,
                                RedirectAttributes rattrs) {

        User user = userService.findByUsername(principal.getName());

        user.setFirstname(changedUser.getFirstname());
        user.setLastname(changedUser.getLastname());
        user.setPassword(passwordEncoder(changedUser));

        if (!user.getEmail().equals(changedUser.getEmail())) {
            if (userService.isExistByEmail(changedUser.getEmail())) {
                rattrs.addFlashAttribute("duplicateField", "email");
                return "redirect:/profile/data";
            }
            user.setEmail(changedUser.getEmail());
            if (!userService.hasRole(user, "ADMIN")) {
                user.getRoles().clear();
                user.getRoles().add(roleService.createOrFoundRoleByName("GUEST"));
                emailSenderService.sendVerificationEmail(user);
                return "verify_message";
            }
        }
        userService.save(user);
        return "redirect:/";
    }

    @GetMapping("/profile/companies")
    public String getCompanies(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        List<Company> companies;

        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        if (userService.hasAuthority(user, "ADMIN")) {
            companies = companyService.findAll();
        }
        else {
            companies = companyService.findByUserId(user.getId());
        }
        companyService.calculateAverageForCompanies(companies);
        model.addAttribute("companies", companies);

        return "companies";
    }

    @GetMapping("/profile/companies/favourite")
    public String getFavouriteCompanies(Principal principal, Model model) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        User user = userService.findByUsername(principal.getName());
        List<Company> companies = new ArrayList<>(user.getFavoriteCompanies());
        companyService.calculateAverageForCompanies(companies);
        model.addAttribute("companies", companies);
        model.addAttribute("favouriteCompanies", user.getFavoriteCompanies());
        return "companies";
    }

    @GetMapping("/profile/create/company")
    public String newCompanyForm(@ModelAttribute("isUnique") String isUnique, Principal principal,
                                 @ModelAttribute("isDateWrong") String isDateWrong, Model model) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        model.addAttribute("company", new Company());
        return "profile_create_company";
    }

    @PostMapping("/profile/create/company")
    public String createCompany(@ModelAttribute Company company, Principal principal, RedirectAttributes attrs) {

        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime localDateTime = LocalDate.parse(company.getExpirationDate(), formatter).atStartOfDay();

        if (Timestamp.valueOf(localDateTime).before(Timestamp.from(Instant.now()))) {
            attrs.addAttribute("isDateWrong", "1");
            return "redirect:/profile/create/company";
        }
        if (companyService.isExistByCompanyName(company.getName())) {
            attrs.addAttribute("isUnique", "0");
            return "redirect:/profile/create/company/";
        } else {
            User user = userService.findByUsername(principal.getName());
            company.setUser(user);
            company.setCurrentSum(0.0f);
            company.setYoutubeURL(buildEmbedUrl(company.getYoutubeURL()));
            companyService.save(company);
        }
        return "redirect:/companies";
    }

    @GetMapping("/profile/bonuses")
    public String getBonuses(Model model, Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        User user = userService.findByUsername(principal.getName());
        Set<Bonus> bonuses = bonusService.findByUser(user);

        model.addAttribute("bonuses", bonuses);

        return "profile_bonuses";
    }

    @GetMapping("profile/create/bonus")
    public String newBonusForm(@ModelAttribute("companyId") Long companyId, Model model, Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        model.addAttribute("bonus", new Bonus());
        model.addAttribute("companyId", companyId);

        return "profile_create_bonus";
    }

    @PostMapping("profile/create/bonus")
    public String createBonus(@ModelAttribute Bonus bonus, @ModelAttribute("companyId") Long companyId,
                              RedirectAttributes rattrs, Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        Company company = companyService.findById(companyId);
        bonus.setCompany(company);
        bonusService.save(bonus);

        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @GetMapping("profile/edit/company")
    public String editCompanyForm(@ModelAttribute("companyId") Long companyId, Model model,
                                  Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        Company company = companyService.findById(companyId);

        model.addAttribute("company", company);

        return "profile_edit_company";
    }

    @PostMapping("profile/edit/company")
    public String changeCompany(@ModelAttribute Company company, @ModelAttribute("companyId") Long companyId,
                                RedirectAttributes rattrs, Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        Company changedCompany = companyService.findById(companyId);

        changedCompany.setTopic(company.getTopic());
        changedCompany.setTags(company.getTags());
        changedCompany.setDescription(company.getDescription());
        changedCompany.setYoutubeURL(buildEmbedUrl(company.getYoutubeURL()));
        changedCompany.setCompanyGoal(company.getCompanyGoal());
        changedCompany.setExpirationDate(company.getExpirationDate());

        companyService.save(changedCompany);
        rattrs.addAttribute("companyId", companyId);

        return "redirect:/companies/detail";
    }

    @GetMapping("profile/delete/company")
    public String deleteCompany(@ModelAttribute("companyId") Long companyId, Model model,
                                Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        model.addAttribute("companyId", companyId);

        return "profile_delete_company_confirm";
    }

    @PostMapping(value = "/profile/delete/company/confirm", params = "cancel")
    public String cancelDeleteCompany(@ModelAttribute("companyId") Long companyId, Model model,
                                      Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        model.addAttribute("companyId", companyId);

        return "redirect:/companies/detail?companyId=" + companyId;
    }

    @PostMapping(value = "/profile/delete/company/confirm", params = "confirm")
    public String confirmDeleteCompany(@ModelAttribute("companyId") Long companyId, Principal principal) {
        if (!userService.hasAuthority(userService.findByUsername(principal.getName()), "USER")) {
            return "redirect:/verify";
        }
        companyService.deleteById(companyId);
        return "redirect:/companies";
    }
}
