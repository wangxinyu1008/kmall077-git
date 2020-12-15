package com.kgc.kmall.user.controller;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-15 17:14
 */
@Controller
public class MemberController {
    @Reference
    MemberService memberService;
    @RequestMapping("/")
    @ResponseBody
    public List<Member> test(){
        List<Member> members = memberService.memberList();
        return members;
    }

}
