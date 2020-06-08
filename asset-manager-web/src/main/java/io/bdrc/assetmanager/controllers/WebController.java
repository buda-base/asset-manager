package io.bdrc.assetmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    /**
     * index maps to /, and returns a template (resources/templates/index.html)
     * * @return
     */
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}
