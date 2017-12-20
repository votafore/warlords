package com.votafore.warlords.v2;

import android.net.nsd.NsdServiceInfo;

/**
 * @author Votafore
 * Created  on 18.12.2017.
 *
 * contains an information about server
 */

public class ServiceInfo{

    public static final String FAIL_DISCOVERYSTART      = "StartDiscoveryFailed";
    public static final String FAIL_DISCOVERYSTOP       = "StopDiscoveryFailed";
    public static final String SUCCESS_DISCOVERYSTART   = "StartDiscoverySuccess";
    public static final String SUCCESS_DISCOVERYSTOP    = "StopDiscoverySuccess";
    public static final String SERVICE_FOUND            = "ServiceFound";
    public static final String SERVICE_LOST             = "ServiceLost";

    public static final String REGISTRATION_fAIL       = "RegistrationFail";
    public static final String REGISTRATION_SUCCESS    = "RegistrationSuccess";
    public static final String UNREGISTRATION_fAIL     = "UnregistrationFail";
    public static final String UNREGISTRATION_SUCCESS  = "UnregistrationSuccess";

    public String         messageType;
    public NsdServiceInfo info;
    public int            errorCode;
    public String         serviceType;

    @Override
    public String toString() {
        return String.format("msgType: %s", messageType);
    }
}
