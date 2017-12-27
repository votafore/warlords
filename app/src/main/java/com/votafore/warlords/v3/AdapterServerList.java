package com.votafore.warlords.v3;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.votafore.warlords.R;
import com.votafore.warlords.v3.App;
import com.votafore.warlords.v2.Channel;
import com.votafore.warlords.v2.Constants;
import com.votafore.warlords.v2.IChannel;
import com.votafore.warlords.v2.ServiceInfo;
import com.votafore.warlords.v2.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.ConnectableObservable;


/**
 * @author Votafore
 * Created on 18.12.2017.
 */

public class AdapterServerList extends RecyclerView.Adapter<AdapterServerList.ViewHolder>{

    String TAG = Constants.TAG;
    String prefix= Constants.PFX_ADAPTER;

    String format1 = Constants.format1;
    String format2 = Constants.format2;
    String format3 = Constants.format3;
    String format4 = Constants.format4;

    private Context mContext;

    private List<ListItem> mList;

    public AdapterServerList(Context c){
        mContext = c;
        mList = new ArrayList<>();
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

            Log.v(TAG, String.format(format2, prefix, "RECYCLER_VIEW", "onClick"));

            try {
                mList.get(getAdapterPosition()).send(new JSONObject("{type:request, data:ServerInfo}"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            App app = (App) mContext.getApplicationContext();
//            app.setSelected(mList.get(getAdapterPosition()));
//
//            Intent i = new Intent(mContext, ActivityGame.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            mContext.startActivity(i);
        }
    }






    /******************* Service scanning ************************/

    private CompositeDisposable dsp_scanner;

    public void stopScan(){
        Log.v(TAG, String.format(format1, prefix, "stopScan"));
        dsp_scanner.dispose();
    }

    public void startScan(final Context context){

        Log.v(TAG, String.format(format1, prefix, "startScan"));

        dsp_scanner = new CompositeDisposable();

        // common observable that generates initial data
        // in code below two subscribers are defined. They handle with that data
        ConnectableObservable<ServiceInfo> obs_discovery = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ServiceInfo> e){

                final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                final NsdManager.DiscoveryListener listener = new NsdManager.DiscoveryListener() {
                    @Override
                    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                        //Log.v("TESTRX", "onStartDiscoveryFailed");
                        // TODO: 22.12.2017 handler ommited
                        //e.onError(new Throwable(serviceType));
                    }

                    @Override
                    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                        //Log.v("TESTRX", "onStopDiscoveryFailed");
                        // TODO: 22.12.2017 handler ommited
                        //e.onError(new Throwable(serviceType));
                    }

                    @Override
                    public void onDiscoveryStarted(String serviceType) {

                        // TODO: 22.12.2017 check if in is necessary

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

                        //Log.v("TESTRX", "onServiceFound");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_FOUND;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }

                    @Override
                    public void onServiceLost(NsdServiceInfo serviceInfo) {

                        //Log.v("TESTRX", "onServiceLost");

                        ServiceInfo svc_info = new ServiceInfo();
                        svc_info.messageType = ServiceInfo.SERVICE_LOST;
                        svc_info.info = serviceInfo;

                        e.onNext(svc_info);
                    }
                };

                manager.discoverServices(Constants.SERVICETYPE, NsdManager.PROTOCOL_DNS_SD, listener);

                Log.v(TAG, String.format(format2, prefix, "OBSERVABLE - NSD", "discovery started"));

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        manager.stopServiceDiscovery(listener);
                        Log.v(TAG, String.format(format3, prefix, "OBSERVABLE - NSD", "CANCEL", "discovery stopped (cancelled)"));
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

                        Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "FOUND", "create observable for resolving"));

                        return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                            @Override
                            public void subscribe(final ObservableEmitter<ServiceInfo> e) {

                                Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "FOUND", "create observable for resolving - subscribe"));

                                NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                                manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                    @Override
                                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                        //Log.v("TESTRX", "2. onResolveFailed");
                                        // TODO: 21.12.2017 consumer is missed
                                        //e.onError(new Throwable(String.valueOf(errorCode)));
                                    }

                                    @Override
                                    public void onServiceResolved(NsdServiceInfo serviceInfo) {

                                        Log.v(TAG, String.format(format4, prefix, "OBSERVABLE", "FOUND", "RESOLVE", "resolved"));

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

                        // or this game was created on current device
                        if(((App)context.getApplicationContext()).getDeviceIP().equals(serviceInfo.info.getHost().toString().replace("/", "")))
                            return false;

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

                                Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "FOUND", "create remote server"));

                                IServer server = new ServerRemote(serviceInfo.info.getHost(), serviceInfo.info.getPort());

                                Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "FOUND", "create ListItem"));

                                ListItem item = new ListItem(server);
                                item.setListener(new ListItem.IItemChangeListener() {
                                    @Override
                                    public void onChange(ListItem item) {
                                        Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "adapter listener", "new data received"));
                                        notifyItemChanged(mList.indexOf(item));
                                    }
                                });

                                Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "FOUND", "start server"));
                                server.start(mContext);

                                Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "FOUND", "send request"));
                                server.send(new JSONObject("{type:request, data:ServerInfo}"));

                                e.onNext(item);
                            }
                        });
                    }
                })
                .subscribe(new Consumer<ListItem>() {
                    @Override
                    public void accept(ListItem item) throws Exception {
                        Log.v(TAG, String.format(format4, prefix, "OBSERVABLE", "LOST", "FINAL", "add item to list"));
                        mList.add(item);
                        notifyItemInserted(mList.size()-1);
                    }
                }));



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

                                Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "LOST", "create observable for resolving"));

                                return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                                    @Override
                                    public void subscribe(final ObservableEmitter<ServiceInfo> e) {
                                        //Log.v("TESTRX", "2. subscribe");
                                        NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
                                        manager.resolveService(serviceInfo.info, new NsdManager.ResolveListener() {
                                            @Override
                                            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                                                //Log.v("TESTRX", "2. onResolveFailed");
                                                // TODO: 21.12.2017 consumer is missed
                                                //e.onError(new Throwable(String.valueOf(errorCode)));
                                            }

                                            @Override
                                            public void onServiceResolved(NsdServiceInfo serviceInfo) {

                                                Log.v(TAG, String.format(format4, prefix, "OBSERVABLE", "LOST", "RESOLVE", "resolved"));

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

                                    Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "LOST", "stop item"));
                                    item.stop();

                                    Log.v(TAG, String.format(format3, prefix, "OBSERVABLE", "LOST", "delete from list"));
                                    mList.remove(item);

                                    notifyItemRemoved(index);

                                    break;
                                }
                            }
                        })
        );

        dsp_scanner.add(obs_discovery.connect());

    }



    private ListItem mLocalItem;

    public void addLocalServer(IServer server){

        final String localTAG = "addLocalServer";

        Log.v(TAG, String.format(format1, prefix, localTAG));

        Log.v(TAG, String.format(format2, prefix, localTAG, "create ListItem"));
        ListItem item = new ListItem(server);

        Log.v(TAG, String.format(format2, prefix, "adapter listener", "set listener to item"));
        item.setListener(new ListItem.IItemChangeListener() {
            @Override
            public void onChange(ListItem item) {
                Log.v(TAG, String.format(format2, prefix, "adapter listener", "new data received"));
                notifyItemChanged(mList.indexOf(item));
            }
        });

        mLocalItem = item;

        Log.v(TAG, String.format(format2, prefix, localTAG, "add item to list"));
        mList.add(mLocalItem);
        notifyItemInserted(mList.size()-1);


        try {
            Log.v(TAG, String.format(format2, prefix, localTAG, "send request"));
            item.send(new JSONObject("{type:request, data:ServerInfo}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeLocalServer(){

        Log.v(TAG, String.format(format1, prefix, "removeLocalServer"));
        int index = mList.indexOf(mLocalItem);

        mList.remove(index);
        Log.v(TAG, String.format(format2, prefix, "removeLocalServer", "ListItem has been removed"));
        notifyItemRemoved(index);
    }


}
