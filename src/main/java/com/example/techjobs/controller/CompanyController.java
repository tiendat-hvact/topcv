package com.example.techjobs.controller;

import com.example.techjobs.common.mapper.GenericMapper;
import com.example.techjobs.common.util.Utils;
import com.example.techjobs.dto.NotificationRequest;
import com.example.techjobs.dto.ResultDTO;
import com.example.techjobs.dto.SearchRequest;
import com.example.techjobs.dto.inputDTO.InputCompanyDTO;
import com.example.techjobs.dto.inputDTO.InputJobDTO;
import com.example.techjobs.dto.outputDTO.OutputApplyDTO;
import com.example.techjobs.dto.outputDTO.OutputCompanyDTO;
import com.example.techjobs.dto.outputDTO.OutputJobDTO;
import com.example.techjobs.entity.ApplyId;
import com.example.techjobs.entity.Job;
import com.example.techjobs.service.ApplyService;
import com.example.techjobs.service.CompanyService;
import com.example.techjobs.service.JobService;
import com.example.techjobs.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/techJob/company")
public class CompanyController {

  private final CompanyService companyService;
  private final GenericMapper genericMapper;
  private final ApplyService applyService;
  private final TypeService typeService;
  private final JobService jobService;

  @Autowired
  public CompanyController(
      CompanyService companyService,
      GenericMapper genericMapper,
      ApplyService applyService,
      TypeService typeService,
      JobService jobService) {
    this.companyService = companyService;
    this.genericMapper = genericMapper;
    this.applyService = applyService;
    this.typeService = typeService;
    this.jobService = jobService;
  }

  @GetMapping
  public String companyFindById(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "company", defaultValue = "0") Integer companyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputCompanyDTO company = companyService.findById(companyId);
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "update-fail":
          notification.setText(
              "C???p nh???t th??ng tin th???t b???i <br> Email m???i c???a c??ng ty ???? c?? trong h??? th???ng");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("company", genericMapper.mapToType(company, InputCompanyDTO.class));
    model.addAttribute("numberJob", company.getNumberJob());
    model.addAttribute("notification", notification);
    return "company-info";
  }

  @PostMapping("/update-info")
  public String companyUpdate(
      @CookieValue(name = "company", defaultValue = "0") Integer company,
      @ModelAttribute InputCompanyDTO data) {
    if (companyService.updateCompany(company, data)) {
      return "redirect:/techJob/company";
    } else {
      return "redirect:/techJob/company?text=update-fail";
    }
  }

  @GetMapping("/get-form-create-job")
  public String getFormCreateJob(
      Model model,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "company", defaultValue = "0") Integer companyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "invalid-info":
          notification.setText(
              "T???o tin tuy???n d???ng th???t b???i <br> Xin h??y nh???p ?????y ????? th??ng tin tr?????c khi l??u");
          break;
        case "create-fail":
          notification.setText(
              "T???o tin tuy???n d???ng th???t b???i <br> Th??ng tin c??ng ty kh??ng c?? trong h??? th???ng");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("job", new InputJobDTO());
    model.addAttribute("company", companyService.findById(companyId));
    model.addAttribute("notification", notification);
    model.addAttribute("listType", typeService.findAll());
    return "create-job";
  }

  @PostMapping("/create-job")
  public String createJob(
      @CookieValue(name = "company", defaultValue = "0") Integer companyId,
      @ModelAttribute InputJobDTO data) {
    if (Utils.isNullOrEmpty(data.getName())
        || Utils.isNullOrEmpty(String.valueOf(data.getNumberRecruit()))
        || Utils.isNullOrEmpty(data.getSalary())
        || Utils.isNullOrEmpty(data.getExperience())
        || Utils.isNullOrEmpty(data.getWorkingForm())
        || data.getDeadline() == null
        || Utils.isNullOrEmpty(data.getWelfare())
        || Utils.isNullOrEmpty(data.getDescription())
        || Utils.isNullOrEmpty(data.getRequirement())) {
      return "redirect:/techJob/company/get-form-create-job?text=invalid-info";
    }
    Integer jobId = jobService.createJob(companyId, data);
    if (jobId != 0) {
      return "redirect:/techJob/job/" + jobId;
    } else {
      return "redirect:/techJob/company/get-form-create-job?text=create-fail";
    }
  }

  @GetMapping("/get-form-update-job/{jobId}")
  public String getFormUpdateJob(
      Model model,
      @PathVariable(name = "jobId") Integer jobId,
      @ModelAttribute(name = "notification") NotificationRequest notification,
      @CookieValue(name = "company", defaultValue = "0") Integer companyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    OutputJobDTO job = jobService.findById(jobId);
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "invalid-info":
          notification.setText(
              "C???p nh???t th??ng tin tuy???n d???ng th???t b???i <br> Xin h??y nh???p ?????y ????? th??ng tin tr?????c khi l??u");
          break;
        case "update-success":
          notification.setText("C???p nh???t th??ng tin tuy???n d???ng th??nh c??ng");
          break;
        case "update-fail":
          notification.setText(
              "C???p nh???t th??ng tin tuy???n d???ng th???t b???i <br> Xin h??y ki???m tra th??ng tin nh???p v??o v?? h??y ch???c ch???n t??n tin tuy???n d???ng kh??ng b??? tr??ng");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("jobId", job.getId());
    model.addAttribute("job", genericMapper.mapToType(job, InputJobDTO.class));
    model.addAttribute("company", companyService.findById(companyId));
    model.addAttribute("notification", notification);
    model.addAttribute("listType", typeService.findAll());
    return "update-job";
  }

  @PostMapping("/update-job/{jobId}")
  public String updateJob(
      @PathVariable(name = "jobId") Integer jobId, @ModelAttribute InputJobDTO data) {
    if (Utils.isNullOrEmpty(data.getName())
        || Utils.isNullOrEmpty(String.valueOf(data.getNumberRecruit()))
        || Utils.isNullOrEmpty(data.getSalary())
        || Utils.isNullOrEmpty(data.getExperience())
        || Utils.isNullOrEmpty(data.getWorkingForm())
        || data.getDeadline() == null
        || Utils.isNullOrEmpty(data.getWelfare())
        || Utils.isNullOrEmpty(data.getDescription())
        || Utils.isNullOrEmpty(data.getRequirement())) {
      return "redirect:/techJob/company/get-form-update-job/" + jobId + "?text=invalid-info";
    }
    boolean success = jobService.updateJob(jobId, data);
    if (success) {
      return "redirect:/techJob/company/get-form-update-job/" + jobId + "?text=update-success";
    } else {
      return "redirect:/techJob/company/get-form-update-job/" + jobId + "?text=update-fail";
    }
  }

  @GetMapping("/delete-job/{jobId}")
  public String deleteJob(
      @PathVariable(name = "jobId") Integer jobId,
      @CookieValue(name = "company", defaultValue = "0") Integer companyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    ResultDTO<Job> result = this.jobService.delete(jobId);
    if (result.isError()) {
      return "redirect:/techJob/company/job-management/page/1?text=delete-fail";
    } else {
      return "redirect:/techJob/company/job-management/page/1?text=delete-success";
    }
  }

  @RequestMapping("/job-management/page/{pageId}")
  public String jobManagement(
      Model model,
      @PathVariable(name = "pageId") Integer pageId,
      @CookieValue(name = "company", defaultValue = "0") Integer companyId,
      @ModelAttribute(name = "searchRequest") SearchRequest searchRequest,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    int size = 5;
    searchRequest.setCompanyId(companyId);
    Page<OutputJobDTO> outputJobDTOS =
        jobService.getPageableJobByCondition(searchRequest, pageId, size);
    if (outputJobDTOS == null || outputJobDTOS.isEmpty()) {
      model.addAttribute("page", 0);
      model.addAttribute("size", 0);
      model.addAttribute("pageId", 0);
      model.addAttribute("stt", 0);
      model.addAttribute("jobs", null);
    } else {
      int page = outputJobDTOS.getTotalPages();
      model.addAttribute("page", page);
      model.addAttribute("size", size);
      model.addAttribute("pageId", pageId);
      model.addAttribute("stt", Utils.getListNumberPage(page));
      model.addAttribute("jobs", outputJobDTOS);
    }
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "delete-success":
          notification.setText("X??a tin tuy???n d???ng th??nh c??ng");
          break;
        case "delete-fail":
          notification.setText(
              "X??a tin tuy???n d???ng th???t b???i <br> Xin h??y ch???c ch???n tin tuy???n d???ng c?? t???n t???i");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("notification", notification);
    model.addAttribute("searchRequest", new SearchRequest());
    return "company-job-management";
  }

  @RequestMapping("/candidate-management/page/{pageId}")
  public String candidateManagement(
      Model model,
      @PathVariable(name = "pageId") Integer pageId,
      @CookieValue(name = "company", defaultValue = "0") Integer companyId,
      @ModelAttribute(name = "searchRequest") SearchRequest searchRequest,
      @ModelAttribute(name = "notification") NotificationRequest notification) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    int size = 5;
    searchRequest.setCompanyId(companyId);
    Page<OutputApplyDTO> outputApplyDTOS =
        applyService.getPageableApplyByCondition(searchRequest, pageId, size);
    if (outputApplyDTOS == null || outputApplyDTOS.isEmpty()) {
      model.addAttribute("page", 0);
      model.addAttribute("size", 0);
      model.addAttribute("pageId", 0);
      model.addAttribute("stt", 0);
      model.addAttribute("candidates", null);
    } else {
      int page = outputApplyDTOS.getTotalPages();
      model.addAttribute("page", page);
      model.addAttribute("size", size);
      model.addAttribute("pageId", pageId);
      model.addAttribute("stt", Utils.getListNumberPage(page));
      model.addAttribute("candidates", outputApplyDTOS);
    }
    if (notification.getText() != null) {
      switch (notification.getText()) {
        case "delete-success":
          notification.setText("X??a th??ng tin ???ng tuy???n th??nh c??ng");
          break;
        case "delete-fail":
          notification.setText(
              "X??a th??ng tin ???ng tuy???n th???t b???i <br> Xin h??y ch???c ch???n th??ng tin c?? t???n t???i");
          break;
        default:
          notification.setText(null);
          break;
      }
    }
    model.addAttribute("notification", notification);
    model.addAttribute("searchRequest", new SearchRequest());
    return "company-candidate-management";
  }

  @GetMapping("/delete-candidate")
  public String deleteCandidate(
      @CookieValue(name = "company", defaultValue = "0") Integer companyId,
      @ModelAttribute ApplyId applyId) {
    if (companyId == 0) {
      return "redirect:/techJob/login?text=unauthorized";
    }
    if (applyService.deleteApply(applyId)) {
      return "redirect:/techJob/company/candidate-management/page/1?text=delete-success";
    } else {
      return "redirect:/techJob/company/candidate-management/page/1?text=delete-fail";
    }
  }
}
