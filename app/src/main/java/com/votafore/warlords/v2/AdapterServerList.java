package com.votafore.warlords.v2;

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
import com.votafore.warlords.v2.test.Channel;
import com.votafore.warlords.v2.test.Constants;
import com.votafore.warlords.v2.test.IChannel;
import com.votafore.warlords.v2.test.Socket;

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


/**
 * @author Votafore
 * Created on 18.12.2017.
 */

public class AdapterServerList extends RecyclerView.Adapter<AdapterServerList.ViewHolder>{

    public AdapterServerList(){
        mList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = View.inflate(parent.getContext(), R.layout.item_found_game, null);
        return new AdapterServerList.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ListItem item = mList.get(position);

        //holder.mImageView.setImageResource(current.mResMap);
        holder.mOwnerName.setText(item.ownerName);
        holder.mPlayerCount.setText(item.playerCount);
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
            //Log.v("TESTRX", ">>>>>>>>> request: get info");
            try {
                mList.get(getAdapterPosition()).getChanel().getSender().onNext(new JSONObject("{type:request, data:ServerInfo}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }






    /******************* Service scanning ************************/

    private CompositeDisposable dsp_scanner;

    public void stopScan(){
        dsp_scanner.dispose();
    }

    public void startScan(final Context context){

        dsp_scanner = new CompositeDisposable();

        // common observable that generates initial data
        // in code below two subscribers are defined. They handle with that data
        Observable<ServiceInfo> obs_discovery = Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
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

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
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
        });

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

                        return Observable.create(new ObservableOnSubscribe<ServiceInfo>() {
                            @Override
                            public void subscribe(final ObservableEmitter<ServiceInfo> e) {

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

                                ListItem item = new ListItem();
                                item.connectTo(serviceInfo.info.getHost(), serviceInfo.info.getPort());
                                item.getChanel().getSender().onNext(new JSONObject("{type:request, data:ServerInfo}"));
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
                                                //Log.v("TESTRX", "2. onServiceResolved");

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
                        // find ListItem that matches given IP
                        .flatMap(new Function<ServiceInfo, Observable<ListItem>>() {
                            @Override
                            public Observable<ListItem> apply(final ServiceInfo serviceInfo) throws Exception {
                                // TODO: 23.12.2017 may be this step is redundant, it might be done in last step
                                return Observable.create(new ObservableOnSubscribe<ListItem>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<ListItem> e) throws Exception {

                                        for (ListItem item : mList) {
                                            if(item.toString().equals(serviceInfo.info.getHost().toString()))
                                                e.onNext(item);
                                        }
                                    }
                                });
                            }
                        })
                        .subscribe(new Consumer<ListItem>() {
                            @Override
                            public void accept(ListItem item) throws Exception {

                                int index = mList.indexOf(item);

                                item.getChanel().getSender().onComplete();
                                mList.remove(item);

                                notifyItemRemoved(index);
                            }
                        })
        );

    }






    /********************* IAdapter *****************/

    private List<ListItem> mList;

    public class ListItem{

        String ownerName;
        String playerCount;

        private InetAddress mIP;

        private Channel mChanel;

        private Disposable dsp_socket;

        public ListItem(){

            //Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel");

            mChanel = new Channel();

            //Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel. created");

            mChanel.setReceiver(new Consumer<JSONObject>() {
                @Override
                public void accept(JSONObject jsonObject) throws Exception {

                    Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel. receiver is called");

                    try {
                        ownerName = jsonObject.getString("owner");
                        playerCount = jsonObject.getString("playerCount");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AdapterServerList.this.notifyItemChanged(mList.indexOf(ListItem.this));
                }
            });

        }

        public void connectTo(final InetAddress ip, final int port){

            mIP = ip;

            //Log.v("TESTRX", ">>>>>>>>> ListItem - createChanel. receiver is set");

            //Log.v("TESTRX", ">>>>>>>>> subscribing for adding socket in list once");
            dsp_socket = Observable.create(new ObservableOnSubscribe<Socket>() {
                @Override
                public void subscribe(ObservableEmitter<Socket> e) throws Exception {
                    //Log.v("TESTRX", ">>>>>>>>> subscribing for adding socket in list once - create socket");
                    Socket s = new Socket(ip, port);
                    e.onNext(s);

                    e.onComplete();
                }
            }).subscribe(mChanel.getSubscriber());

        }

        // TODO: 23.12.2017 create local connection




        public IChannel getChanel(){
            return mChanel;
        }

        public void close(){
            dsp_socket.dispose();
        }

        @Override
        public String toString() {
            return mIP.toString();
        }
    }
}
