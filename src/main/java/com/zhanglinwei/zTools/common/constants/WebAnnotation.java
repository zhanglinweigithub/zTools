package com.zhanglinwei.zTools.common.constants;

import com.google.common.collect.ImmutableSet;

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
        return ImmutableSet.of(RequestParam, PathVariable, RequestHeader, RequestPart, RequestBody);
    }

}
