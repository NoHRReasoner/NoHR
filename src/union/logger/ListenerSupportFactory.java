package union.logger;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListenerSupportFactory {
	 
    private ListenerSupportFactory() {}
 
    @SuppressWarnings("unchecked")
    public static <T> T createListenerSupport(Class<T> listenerInterface) {
        return (T)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { IListenerSupport.class, listenerInterface }, new ListenerInvocationHandler<T>(listenerInterface));
    }
 
    private static class ListenerInvocationHandler<T> implements InvocationHandler {
 
        private final Class<T> _listener_iface;
 
        private final List<T> _listeners = Collections.synchronizedList(new ArrayList<T>());
        private final Set<String> _current_events = Collections.synchronizedSet(new HashSet<String>());
 
        private ListenerInvocationHandler(Class<T> listenerInterface) {
            _listener_iface = listenerInterface;

        }
 
        @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            // (1) handle IListenerSupport methods
            if (method.getDeclaringClass().equals(IListenerSupport.class)) {
                if ("addListener".equals(methodName)) {
                    _listeners.add( (T)args[0] );
                } else if ("removeListener".equals(methodName)) {
                    _listeners.remove( args[0] );
                }
                return null;
            }
            // (2) handle listener interface
            if (method.getDeclaringClass().equals(_listener_iface)) {
                if (_current_events.contains(methodName)) {
                    //throw new RuleViolationException("Cyclic event invocation detected: " + methodName);
                }
                _current_events.add(methodName);
                for (T listener : _listeners) {
                    try {
                        method.invoke(listener, args);
                    } catch (Exception ex) {
                       // _log.error("Listener invocation failure", ex);
                    }
                }
                _current_events.remove(methodName);
                return null;
            }
            // (3) handle all other stuff (equals(), hashCode(), etc.)
            return method.invoke(this, args);
        }
    }
 
}