package net.nuagenetworks.vro.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.nuagenetworks.bambou.RestException;
import net.nuagenetworks.bambou.RestObject;
import net.nuagenetworks.bambou.RestRootObject;
import net.nuagenetworks.bambou.RestSession;

public class BaseRootObject extends RestRootObject implements BaseObjectExtensions {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private BaseSession<?> session;

    public void setSession(BaseSession<?> session) {
        this.session = session;
    }

    public BaseSession<?> getSession() {
        return session;
    }

    @Override
    public void fetch(RestSession<?> session) throws RestException {
        super.fetch(session);

        setSession((BaseSession<?>) session);

        // Set Root's ID as the same as the session ID
        setId(((BaseSession<?>) session).getId());
    }

    @Override
    public void save(RestSession<?> session, Integer responseChoice) throws RestException {
        super.save(session, responseChoice);

        setSession((BaseSession<?>) session);
    }

    @Override
    public void delete(RestSession<?> session, Integer responseChoice) throws RestException {
        super.delete(session, responseChoice);

        setSession((BaseSession<?>) session);
    }

    @Override
    public void createChild(RestSession<?> session, RestObject childRestObj, Integer responseChoice, boolean commit) throws RestException {
        super.createChild(session, childRestObj, responseChoice, commit);

        ((BaseObjectExtensions) childRestObj).setSession((BaseSession<?>) session);
        setSession((BaseSession<?>) session);
    }

    @Override
    public void instantiateChild(RestSession<?> session, RestObject childRestObj, RestObject fromTemplate, Integer responseChoice, boolean commit)
            throws RestException {
        super.instantiateChild(session, childRestObj, fromTemplate, responseChoice, commit);

        ((BaseObjectExtensions) childRestObj).setSession((BaseSession<?>) session);
        setSession((BaseSession<?>) session);
    }

    @Override
    public void assign(RestSession<?> session, java.util.List<? extends RestObject> childRestObjs, boolean commit) throws RestException {
        super.assign(session, childRestObjs, commit);

        setSession((BaseSession<?>) session);
    }
}
