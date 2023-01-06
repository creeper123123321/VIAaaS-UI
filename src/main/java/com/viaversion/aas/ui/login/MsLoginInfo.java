package com.viaversion.aas.ui.login;

import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.viaversion.aas.ui.VIAaaSUI;

public class MsLoginInfo {
    public MsaAuthenticationService service = new MsaAuthenticationService(VIAaaSUI.CLIENT_ID);
    public boolean generatedCode;
}
