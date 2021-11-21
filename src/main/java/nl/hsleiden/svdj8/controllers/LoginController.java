package nl.hsleiden.svdj8.controllers;

import nl.hsleiden.svdj8.models.tables.Admin;
import nl.hsleiden.svdj8.repository.AdminRepository;
import nl.hsleiden.svdj8.services.HashService;
import nl.hsleiden.svdj8.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class LoginController {
    @Autowired
    AdminRepository adminRepository;

    VerificationService verificationService = new VerificationService();


    @PostMapping("/admin/login")
    public boolean loginUser(@Valid @RequestBody Admin admin) {
        List<Admin> admins = (List<Admin>) adminRepository.findAll();

        for (Admin existingAdmin : admins) {
            if (HashService.comparePassword(existingAdmin.getPassword(), admin.getPassword())) {
                return true; // Should be admin token or better form for authentication
            }
        }
        return false;
    }

    @PostMapping("/admin/resetpasswordrequest")
    public boolean resetPasswordRequest(@Valid @RequestBody Admin admin) {
        List<Admin> admins = (List<Admin>) adminRepository.findAll();

        for (Admin existingAdmin : admins) {
            if (existingAdmin.getEmail().equals(admin.getEmail())) {
                String verificationCode = verificationService.createVerificationCode();
                existingAdmin.setVerificationCode(verificationCode);
                verificationService.sendEmail(admin.getEmail() ,verificationCode);
                return true; // Preferably a token but ill add that later. - Brandon
            }
        }
        return false;
    }

    @PostMapping("/admin/resetpasswordverify")
    public boolean resetPasswordVerification(@Valid @RequestBody Admin admin) {
        String email = admin.getEmail();
        String verificationCode = admin.getVerificationCode();

        if (verificationCode == null) {
            return false;
        }

        List<Admin> admins = (List<Admin>) adminRepository.findAll();
        // Would be better for the verification code to be stored as a token with expiration date.

        for(Admin existingAdmin : admins) {
            if (existingAdmin.getEmail().equals(email) && existingAdmin.getVerificationCode().equals(verificationCode)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/admin/resetpassword")
    public Boolean resetPassword(@Valid @RequestBody Admin admin) {
        List<Admin> admins = (List<Admin>) adminRepository.findAll();

        for(Admin existingAdmin : admins) {
            if (existingAdmin.getEmail().equals(admin.getEmail())
                    && existingAdmin.getVerificationCode().equals(admin.getVerificationCode())) {
                existingAdmin.setPassword(admin.getPassword());
                existingAdmin.setVerificationCode(null);
                return true;
            }
        }
        return false;
    }

}