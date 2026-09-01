package com.zhanglinwei.zTools.common.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface WebAnnotation {

    String Controller = "Controller";
    String RestController = "RestController";

    String RequestMapping = "@RequestMapping";
    String GetMapping = "@GetMapping";
    String PutMapping = "@PutMapping";
    String DeleteMapping = "@DeleteMapping";
    String PatchMapping = "@PatchMapping";

    String RequestParam = "@RequestParam";
    String PathVariable = "@PathVariable";
    String RequestHeader = "@RequestHeader";
    String RequestPart = "@RequestPart";
    String RequestBody = "@RequestBody";
    String ResponseBody = "@ResponseBody";


    static Set<String> webParamAnnotation() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                RequestParam, PathVariable, RequestHeader, RequestPart, RequestBody
        )));
    }

}
