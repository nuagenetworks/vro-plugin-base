/*
  Copyright (c) 2015, Alcatel-Lucent Inc
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
      * Neither the name of the copyright holder nor the names of its contributors
        may be used to endorse or promote products derived from this software without
        specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
