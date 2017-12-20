package com.votafore.warlords;

//import android.app.Instrumentation;
//import android.content.Context;
//import android.net.nsd.NsdManager;
//import android.test.suitebuilder.annotation.SmallTest;
//
//import com.votafore.warlords.game.EndPoint;
//import com.votafore.warlords.support.ListAdapter;
//
//import com.votafore.warlords.support.ServiceScanner;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.Spy;
//import org.mockito.internal.configuration.injection.MockInjection;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import static org.junit.Assert.*;
//
//import static org.mockito.Mockito.*;



//@RunWith(MockitoJUnitRunner.class)
public class GameFactoryTest {

    /**
     * в этом классе тестов проверяется работа класса GameFactory.
     * он может:
     * - реагировать на состояние активити списка серверов
     *      - создано (создается адаптер для списка и сканер)
     *      - видимо (запускается сканер и broadcaster (если он есть))
     *      - остановлено (останавливается сканер и broadcaster (если он есть))
     *
     * -
     */

//    @Mock ServiceScanner    mScanner;
//    @Mock ListAdapter       mAdapter;
//
//    @InjectMocks GameFactory mGameFactory;
//
//    @Mock Context           mContext;
//    @Mock NsdManager        NSDManager;
//
//
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//    }

    /*********************************************************************************/
    /******************** тестирование поведения объектов ****************************/

//    @Test
//    public void testBehaviour_Scanner()throws Exception{
//
//        // у сканера следующее поведение:
//        // - создается при создании активити со списком
//        // - начинает сканирование при событии onResume
//        // - останавливает сканирование при событии onPause
//
//        // когда активити появляется (восстанавливается), то начинается сканирование
//        mGameFactory.onActivityResume();
//        verify(mScanner).startScan();
//
//
//        // когда активити уничтожается (сворачивается), то сканирование прекращается
//        mGameFactory.onActivityPause();
//        verify(mScanner).stopScan();
//    }



    /*********************************************************************************/
    /********************* тестирование контрольных точек ****************************/


//    @Test
//    public void controlPoint_OnActivityCreate()throws Exception{
//
//        // при создании активити должны создаться сканер и адаптер
//        // так же они должны "связаться" т.е. сканер должен получить
//        // ссылку на адаптер для добавления элементов в него
//
//        GameFactory factory = GameFactory.getInstance();
//
//        // убедимся что объекты еще не созданы
//        assertNull(factory.mAdapter);
//        assertNull(factory.mScanner);
//
//        factory.onActivityCreate(mContext);
//
//        // убедимся что объекты уже созданы
//        assertNotNull(factory.mAdapter);
//        assertNotNull(factory.mScanner);
//
//        // убедимся что сканер получил ссылку на адаптер
//        assertNotNull(factory.mScanner.mAdapter);
//    }

//    @Test
//    public void controlPoint_OnActivityCreate_check_mScannerSetAdapterCalled()throws Exception{
//
//        // тест работает только если закомментировать создание объектов в методе
//        // но вообще-то так нельзя
//        // TODO: найти способ как это решить
//
////        mGameFactory.onActivityCreate(mContext);
////        verify(mScanner).setAdapter(mAdapter);
//    }

//    @Test
//    public void controlPoint_OnActivityResume()throws Exception{
//
//        // при появлении активити должно
//        // - запуститься сканирование
//        // - запуститься вещание (если создан сервер).
//        //      Для этого надо тестировать создание сервера и его объектов
//        // скорее всего это будет в других тестах
//
//        mGameFactory.onActivityResume();
//        verify(mScanner).startScan();
//    }
//
//    @Test
//    public void controlPoint_OnActivityPause()throws Exception{
//
//        // при остановке активити должно
//        // - остановиться сканирование
//        // - остановиться вещание (если создан сервер).
//        //      Для этого надо тестировать создание сервера и его объектов
//        // скорее всего это будет в других тестах
//
//        mGameFactory.onActivityPause();
//        verify(mScanner).stopScan();
//    }

//    @Test
//    public void controlPoint_CreateServer()throws Exception{
//
//        // порядок создания сервера
//        // - создаем сервер
//        // - создаем канал связи для сервера
//        // - "связываем" их
//        // - запускаем "добавлятеля" подключений (ServerSocket)
//
//        // - создаем ListAdapter.ListItem, заполняем его и добавляем в список адаптера
//
//        // - создаем NsdServiceInfo для ServiceBroadcaster. Создаем ServiceBroadcaster. Стартуем его.
//
//
//        GameFactory factory = GameFactory.getInstance();
//        assertNull(factory.mServer);
//        assertNull(factory.serverChanel);
//
//
//
//        factory.createServer(mContext);
//
////        EndPoint server = mock(EndPoint.class, "mServer");
////        assertNotNull(factory.mServer);
////        assertNotNull(factory.serverChanel);
//
//    }
}