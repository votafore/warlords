package com.votafore.warlords;

import android.app.Instrumentation;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.test.suitebuilder.annotation.SmallTest;
import com.votafore.warlords.support.ListAdapter;

import com.votafore.warlords.support.ServiceScanner;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.configuration.injection.MockInjection;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Created by admin on 17.10.2016.
 */


@RunWith(MockitoJUnitRunner.class)
public class GameFactoryTest {

    @Mock ServiceScanner    mScanner;
    @Mock ListAdapter       mAdapter;
    //@Mock GameFactory       mGameFactory;

    //@Spy private GameFactory userProvider = new GameFactory();
    @InjectMocks GameFactory mGameFactory;

    @Mock Context           mContext;
    @Mock NsdManager        NSDManager;



    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        //mGameFactory = GameFactory.getInstance();
    }

    @Test
    public void scannerMustStartWhenActivityResume()throws Exception{

        // когда активити появляется (восстанавливается), то начинается сканирование
        mGameFactory.onActivityResume();
        verify(mScanner).startScan();


        // когда активити уничтожается (сворачивается), то сканирование прекращается
        mGameFactory.onActivityPause();
        verify(mScanner).stopScan();

        //verify(mScanner).setAdapter(any(ListAdapter.class));
    }

    @Test
    public void testOnActivityCreate() throws Exception {

    }

    @Test
    public void onActivityResume() throws Exception {

    }

    @Test
    public void onActivityPause() throws Exception {

        mGameFactory.onActivityPause();
    }

    @Test
    public void createServer() throws Exception {

    }

    @Test
    public void startGame() throws Exception {

    }

    @Test
    public void exit() throws Exception {

    }

    @Test
    public void stopServer() throws Exception {

    }

    @Test
    public void getAdapter() throws Exception {

    }

    @Test
    public void someFunc() throws Exception {

    }

    @Test
    public void stopClient() throws Exception {

    }

    @Test
    public void getLocalIpAddress() throws Exception {

    }

    @Test
    public void getSurfaceView() throws Exception {

    }

}