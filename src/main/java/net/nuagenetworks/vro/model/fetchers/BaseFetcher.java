package net.nuagenetworks.vro.model.fetchers;

import net.nuagenetworks.bambou.RestException;
import net.nuagenetworks.bambou.RestFetcher;
import net.nuagenetworks.bambou.RestObject;
import net.nuagenetworks.bambou.RestSession;
import net.nuagenetworks.vro.model.BaseObjectExtensions;
import net.nuagenetworks.vro.model.BaseSession;

public abstract class BaseFetcher<T extends RestObject> extends RestFetcher<T> {
    private static final long serialVersionUID = 1L;

    protected BaseFetcher(RestObject parentRestObj, Class<T> restObjClass) {
        super(parentRestObj, restObjClass);
    }

    public abstract String getId();

    public BaseSession<?> getSession() {
        RestObject obj = getParentRestObj();
        return ((BaseObjectExtensions) obj).getSession();
    }

    @Override
    public java.util.List<T> fetch(RestSession<?> session, String filter, String orderBy, String[] groupBy, Integer page, Integer pageSize,
            String queryParameters, boolean commit) throws RestException {
        java.util.List<T> objs = super.fetch(session, filter, orderBy, groupBy, page, pageSize, queryParameters, commit);

        for (T obj : objs) {
            ((BaseObjectExtensions) obj).setSession((BaseSession<?>) session);
        }

        return objs;
    }

}
