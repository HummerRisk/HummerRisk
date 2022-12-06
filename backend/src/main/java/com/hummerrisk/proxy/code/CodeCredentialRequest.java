package com.hummerrisk.proxy.code;

import com.google.gson.Gson;
import com.hummerrisk.proxy.Request;

import java.io.IOException;

public class CodeCredentialRequest extends Request {

    private CodeCredential codeCredential;

    public CodeCredentialRequest() {
        super("", "");
    }

    public CodeCredentialRequest(Request req) {
        super(req.getCredential(), req.getRegionId());
        setCredential(req.getCredential());
        setRegionId(req.getRegionId());
    }

    public CodeCredential getCodeCredential() {
        if (codeCredential == null) {
            codeCredential = new Gson().fromJson(getCredential(), CodeCredential.class);
        }
        return codeCredential;
    }

    public void setCodeCredential(CodeCredential codeCredential) {
        this.codeCredential = codeCredential;
    }

    public String getToken() {
        codeCredential = getCodeCredential();
        if (codeCredential != null) {
            return codeCredential.getToken();
        }
        return null;
    }

    public String getUrl() {
        codeCredential = getCodeCredential();
        if (codeCredential != null) {
            return codeCredential.getUrl();
        }
        return null;
    }

    public CodeCredential getCodeClient() throws IOException {
        CodeCredential code = getCodeCredential();
        return code;
    }

}
