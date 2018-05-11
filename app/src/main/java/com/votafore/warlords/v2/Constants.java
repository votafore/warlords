//package com.votafore.warlords.v2;
//
///**
// * @author Votafore
// * Created on 22.12.2017.
// */
//
//public final class Constants {
//
//    public static String SERVICENAME = "Warlords";
//    public static String SERVICETYPE = "_http._tcp.";
//
//
//
//
//    /***************** LOGGING *****************/
//
//    public static String format = "%-23s";
//
//    public static String format1 = format + "|| %s";
//    public static String format2 = format + "|| " + format1;
//    public static String format3 = format + "|| " + format2;
//    public static String format4 = format + "|| " + format3;
//
//
//
//    // main/common tag
//    public static String TAG                = "DBG";
//
//    // events
//    public static String CRT                = "CREATE"; // tag for watching how processes creates
//    public static String CLOSE              = "CLOSE"; // tag for watching how processes closes
//    public static String START              = "START"; // tag for watching how processes start
//    public static String STOP               = "STOP"; // tag for watching how processes stop
//    public static String SEND               = "SEND";
//    public static String RECEIVE            = "RECEIVE";
//    //???
//    public static String ADD                = "ADD"; // tag for watching how processes add
//    public static String REMOVE             = "REMOVE"; // tag for watching how processes remove
//
//
//
//    // objects
//    public static String APP                = "APP";  // tag for application processes
//    public static String SRV                = "SRV";  // tag for server processes
//    public static String SOCKET             = "SOCKET";  // tag for socket processes
//    public static String SCAN               = "SCAN";  // tag for scanning/discovering processes
//    public static String DATA               = "DATA";
//
//
//    // prefixes
//    public static String PFX_APP            = "APP";
//    public static String PFX_ADAPTER        = "ADAPTER";
//    public static String PFX_LIST_ITEM      = "LIST_ITEM";
//    public static String PFX_LOCAL_SERVER   = "LOCAL_SERVER";
//    public static String PFX_REMOTE_SERVER  = "REMOTE_SERVER";
//    public static String PFX_SOCKET         = "SOCKET";
//
//
//    // levels
//    public static String LVL_APP            = "APP";
//    public static String LVL_ADAPTER        = "ADAPTER";
//    public static String LVL_LIST_ITEM      = "LIST_ITEM";
//    public static String LVL_LOCAL_SERVER   = "LOCAL_SERVER";
//    public static String LVL_REMOTE_SERVER  = "REMOTE_SERVER";
//    public static String LVL_SOCKET         = "SOCKET";
//    public static String LVL_NW_WATCHER     = "NETWORK_STATE_WATCHER";
//    public static String LVL_SCAN           = "SCANNING";
//
//
//    // combinations of TAGs
//    public static String TAG_APP_START        = String.format(format, TAG + "_" + APP + "_" + START);
//    public static String TAG_APP_STOP         = String.format(format, TAG + "_" + APP + "_" + STOP);
//
//    public static String TAG_SRV_CRT          = String.format(format, TAG + "_" + SRV + "_" + CRT);
//    public static String TAG_SRV_START        = String.format(format, TAG + "_" + SRV + "_" + START);
//    public static String TAG_SRV_STOP         = String.format(format, TAG + "_" + SRV + "_" + STOP);
//
//    public static String TAG_SOCKET           = String.format(format, TAG + "_" + SOCKET);
//    public static String TAG_SOCKET_CRT       = String.format(format, TAG + "_" + SOCKET + "_" + CRT);
//    public static String TAG_SOCKET_CLOSE     = String.format(format, TAG + "_" + SOCKET + "_" + CLOSE);
//
//    public static String TAG_DATA             = String.format(format, TAG + "_" + DATA);
//    public static String TAG_DATA_SEND        = String.format(format, TAG + "_" + DATA + "_" + SEND);
//    public static String TAG_DATA_RECEIVE     = String.format(format, TAG + "_" + DATA + "_" + RECEIVE);
//
//    public static String TAG_SCAN             = String.format(format, TAG + "_" + SCAN);
//    public static String TAG_SCAN_START       = String.format(format, TAG + "_" + SCAN + "_" + START);
//    public static String TAG_SCAN_STOP        = String.format(format, TAG + "_" + SCAN+ "_" + STOP);
//
//
//    // TAG описывает происходящее действие
//    //      - старт/остановка приложения
//    //      - старт/остановка сервера
//    //      - старт/остановка сокета (????)
//
//
//}
