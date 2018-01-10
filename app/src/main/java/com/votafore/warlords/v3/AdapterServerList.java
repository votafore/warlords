package com.votafore.warlords.v3;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.ActivityGame;
import com.votafore.warlords.R;
import com.votafore.warlords.v2.Constants;
import com.votafore.warlords.v2.ServiceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;

import static com.votafore.warlords.v2.Constants.LVL_ADAPTER;
import static com.votafore.warlords.v2.Constants.LVL_SCAN;
import static com.votafore.warlords.v2.Constants.TAG_DATA_RECEIVE;
import static com.votafore.warlords.v2.Constants.TAG_DATA_SEND;
import static com.votafore.warlords.v2.Constants.TAG_SCAN;
import static com.votafore.warlords.v2.Constants.TAG_SCAN_START;
import static com.votafore.warlords.v2.Constants.TAG_SCAN_STOP;
import static com.votafore.warlords.v2.Constants.TAG_SRV_CRT;


/**
 * @author Votafore
 * Created on 18.12.2017.
 */

public class AdapterServerList extends RecyclerView.Adapter<AdapterServerList.ViewHolder>{

    private Context mContext;

    private List<ListItem> mList;

    private Handler mUIHandler;

    public AdapterServerList(Context c){
        mContext = c;
        mList = new ArrayList<>();

        mUIHandler = new Handler();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.item_found_game, null);
        return new AdapterServerList.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        com.votafore.warlords.v3.ListItem item = mList.get(position);

        //holder.mImageView.setImageResource(current.mResMap);
        holder.mOwnerName.setText(item.owner);
        holder.mPlayerCount.setText(item.count);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView mImageView;
        public TextView mOwnerName;
        public TextView  mPlayerCount;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView   = (ImageView) itemView.findViewById(R.id.map_thumbnail);
            mOwnerName   = (TextView) itemView.findViewById(R.id.owner_name);
            mPlayerCount = (TextView) itemView.findViewById(R.id.player_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

//            Log.d1(TAG_DATA_SEND, "LIST ITEM", "onClick... send request");
//            try {
//                mList.get(getAdapterPosition()).send(new JSONObject("{type:request, data:ServerInfo}"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            App app = (App) mContext.getApplicationContext();
            app.setSelected(mList.get(getAdapterPosition()).getServer());

            Intent i = new Intent(mContext, ActivityGame.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(i);
        }
    }






    /******************* Service scanning ************************/

    private CompositeDisposable dsp_scanner;

    public void stopScan(){
        Log.d(TAG_SCAN_STOP, "stopScan");
        dsp_scanner.dispose();
    }

    public void startScan(final Context context){

        Log.d(TAG_SCAN_START, "starting...");

        dsp_scanner = new CompositeDisposable();

        Log.d1(TAG_SCAN_START, LVL_ADAPTER, "create connectable observable");
        // common observable that generates initial data
        // in code below two subscribers are defined. They handle with that data
        ConnectableObservable<ServiceInfo> obs_discovery = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ServiceInfo> e){

                final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                final NsdManager.DiscoveryListener listener = new NsdManager.DiscoveryListener() {
                    @Override
                    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onStartDiscoveryFailed");
                    }

                    @Override
                    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onStopDiscoveryFailed");
                    }

                    @Override
                    public void onDiscoveryStarted(String serviceType) {

                        // TODO: 22.12.2017 check if in is necessary
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onDiscoveryStarted");

//                        Log.v("TESTRX", "onDiscoveryStarted");

//                        ServiceInfo svc_info = new ServiceInfo();
//                        svc_info.messageType = ServiceInfo.SUCCESS_DISCOVERYSTART;
//                        svc_info.serviceType = serviceType;
//
//                        e.onNext(svc_info);
                    }

                    @Override
                    public void onDiscoveryStopped(String serviceType) {

                        // TODO: 22.12.2017 check if in is necessary
                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onDiscoveryStopped");

//                        Log.v("TESTRX", "onDiscoveryStopped");
//
//                        ServiceInfo svc_info = new ServiceInfo();
//                        svc_info.messageType = ServiceInfo.SUCCESS_DISCOVERYSTOP;
//                        svc_info.serviceType = serviceType;
//
//                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceFound(NsdServiceInfo serviceInfo) {

                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceFound");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceLost(NsdServiceInfo serviceInfo) {

                        Log.d1(TAG_SCAN_START, "CONN OBSERVABLE", "onServiceLost");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_LOST;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }
                };

                Log.d2(TAG_SCAN, LVL_ADAPTER, "CONN OBSERVABLE", "create disc. listener, start discover");
                manager.discoverServices(Constants.SERVICETYPE, NsdManager.PROTOCOL_DNS_SD, listener);


                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d2(TAG_SCAN, LVL_ADAPTER, "CONN OBSERVABLE", "cancel. stop discovery");
                        manager.stopServiceDiscovery(listener);
                    }
                });
            }
        })
        // check if it is one of game services
        .filter(new Predicate<ServiceInfo>() {
            @Override
            public boolean test(ServiceInfo serviceInfo) throws Exception {
                return serviceInfo.info.getServiceName().contains(Constants.SERVICENAME);
            }
        }).publish();

        Log.d1(TAG_SCAN_START, LVL_ADAPTER, "add FIND observer");
        // actions when service found
        dsp_scanner.add(

                obs_discovery
                .filter(new Predicate<ServiceInfo>() {
                    @Override
                    public boolean test(ServiceInfo serviceInfo) throws Exception {
                        // only game server that was found is interesting
                        return serviceInfo.messageType.equals(ServiceInfo.SERVICE_FOUND);
                    }
                })
                // convert unresolved NsdInfo into resolved NsdInfo
                .flatMap(new Function<ServiceInfo, Observable<ServiceInfo>>() {
                    @Override
                    public Observable<ServiceInfo> apply(final ServiceInfo serviceInfo) throws Exception {

                        Log.d3(TAG_SCAN, LVL_ADAPTER, "FOUND", "RESOLVE", "create observable");

                        return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                            @Override
                            public void subscribe(final ObservableEmitter<ServiceInfo> e) {

                                Log.d3(TAG_SCAN, LVL_ADAPTER, "FOUND", "RESOLVE", "start");

                                NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                                manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                    @Override
                                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                        Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "FOUND", "RESOLVE", "onResolveFailed");
                                    }

                                    @Override
                                    public void onServiceResolved(NsdServiceInfo serviceInfo) {

                                        Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "FOUND", "RESOLVE", "onServiceResolved");

                                        ServiceInfo svc_info = new ServiceInfo();
                                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                                        svc_info.info = serviceInfo;

                                        e.onNext(svc_info);
                                    }
                                });

                            }
                        });
                    }
                })
                // check if current IP is already added to list
                .filter(new Predicate<ServiceInfo>() {
                    @Override
                    public boolean test(ServiceInfo serviceInfo) throws Exception {

                        for (ListItem item : mList) {
                            if(item.toString().equals(serviceInfo.info.getHost().toString()))
                                return false;
                        }

                        // TODO: 29.12.2017 test
//                        // or this game was created on current device
//                        if(((App)context.getApplicationContext()).getDeviceIP().equals(serviceInfo.info.getHost().toString().replace("/", "")))
//                            return false;

                        return true;
                    }
                })
                // connect to the server and subscribe for his changes
                .flatMap(new Function<ServiceInfo, Observable<ListItem>>() {
                    @Override
                    public Observable<ListItem> apply(final ServiceInfo serviceInfo) throws Exception {

                        return Observable.create(new ObservableOnSubscribe<ListItem>() {
                            @Override
                            public void subscribe(ObservableEmitter<ListItem> e) throws Exception {

                                Log.d2(TAG_SCAN, LVL_ADAPTER, "FOUND", "create remote server");

                                Log.d1(TAG_SRV_CRT, LVL_SCAN, "create");
                                IServer server = new ServerRemote(serviceInfo.info.getHost(), serviceInfo.info.getPort());
                                Log.d1(TAG_SRV_CRT, LVL_SCAN, "created");

                                Log.d1(TAG_SRV_CRT, LVL_SCAN, "start");
                                server.start(mContext);
                                Log.d1(TAG_SRV_CRT, LVL_SCAN, "started");

                                ListItem item = new ListItem(server);
                                item.setListener(new ListItem.IItemChangeListener() {
                                    @Override
                                    public void onChange(ListItem item) {

                                        Log.d2(TAG_DATA_RECEIVE, LVL_ADAPTER, "LIST_ITEM", "data received");
                                        final int index = mList.indexOf(item);

                                        if(index >= 0){
                                            mUIHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyItemChanged(index);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onDelete(final ListItem item) {

                                        final int index = mList.indexOf(item);

                                        item.stop();

                                        if(index >= 0){
                                            mUIHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mList.remove(item);
                                                    notifyItemRemoved(index);
                                                }
                                            });
                                        }
                                    }
                                });

                                Log.d1(TAG_DATA_SEND, LVL_ADAPTER, "send request");
                                server.send(new JSONObject("{type:request, data:ServerInfo}"));

                                e.onNext(item);
                            }
                        });
                    }
                })
                .subscribe(new Consumer<ListItem>() {
                    @Override
                    public void accept(ListItem item) throws Exception {
                        mList.add(item);
                        notifyItemInserted(mList.size()-1);
                    }
                }));

        Log.d1(TAG_SCAN_START, LVL_ADAPTER, "add LOST observer");

        // actions when service lost
        dsp_scanner.add(

                obs_discovery
                        .filter(new Predicate<ServiceInfo>() {
                            @Override
                            public boolean test(ServiceInfo serviceInfo) throws Exception {
                                // only game server that was lost is interesting
                                return serviceInfo.messageType.equals(ServiceInfo.SERVICE_LOST);
                            }
                        })
                        // convert unresolved NsdInfo into resolved NsdInfo
                        .flatMap(new Function<ServiceInfo, Observable<ServiceInfo>>() {
                            @Override
                            public Observable<ServiceInfo> apply(final ServiceInfo serviceInfo) throws Exception {

                                //Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "LOST", "create observable for resolving"));
                                Log.d3(TAG_SCAN, LVL_ADAPTER, "LOST", "RESOLVE", "create observable");

                                return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                                    @Override
                                    public void subscribe(final ObservableEmitter<ServiceInfo> e) {
                                        Log.d3(TAG_SCAN, LVL_ADAPTER, "LOST", "RESOLVE", "start");
                                        NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                                        manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                            @Override
                                            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                                Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "LOST", "RESOLVE", "onResolveFailed");
                                            }

                                            @Override
                                            public void onServiceResolved(NsdServiceInfo serviceInfo) {

                                                Log.d3(TAG_SCAN_START, "CONN OBSERVABLE", "LOST", "RESOLVE", "onServiceResolved");

                                                ServiceInfo svc_info = new ServiceInfo();
                                                svc_info.messageType = ServiceInfo.SERVICE_LOST;
                                                svc_info.info = serviceInfo;

                                                e.onNext(svc_info);
                                            }
                                        });

                                    }
                                });
                            }
                        })
                        // check if current IP is added to list
                        .filter(new Predicate<ServiceInfo>() {
                            @Override
                            public boolean test(ServiceInfo serviceInfo) throws Exception {

                                for (ListItem item : mList) {
                                    if(item.toString().equals(serviceInfo.info.getHost().toString()))
                                        return true;
                                }

                                return false;
                            }
                        })
                        .subscribe(new Consumer<ServiceInfo>() {
                            @Override
                            public void accept(ServiceInfo serviceInfo) throws Exception {

                                for (ListItem item : mList) {

                                    if(!item.toString().equals(serviceInfo.info.getHost().toString())){
                                        continue;
                                    }

                                    int index = mList.indexOf(item);

                                    item.stop();

                                    mList.remove(item);

                                    notifyItemRemoved(index);

                                    break;
                                }
                            }
                        })
        );

        Log.d(TAG_SCAN_START, "start all observables");
        dsp_scanner.add(obs_discovery.connect());

    }



    private ListItem mLocalItem;

    public void addLocalServer(IServer server){

        ListItem item = new ListItem(server);

        item.setListener(new ListItem.IItemChangeListener() {
            @Override
            public void onDelete(ListItem item) {

            }

            @Override
            public void onChange(ListItem item) {
                Log.d2(TAG_DATA_RECEIVE, LVL_ADAPTER, "LIST_ITEM", "data received");
                final int index = mList.indexOf(item);

                if(index >= 0){
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(index);
                        }
                    });
                }


            }
        });

        mLocalItem = item;

        mList.add(mLocalItem);
        notifyItemInserted(mList.size()-1);


        try {
            item.send(new JSONObject("{type:request, data:ServerInfo}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeLocalServer(){

        int index = mList.indexOf(mLocalItem);

        mList.remove(index);
        notifyItemRemoved(index);
    }


}
