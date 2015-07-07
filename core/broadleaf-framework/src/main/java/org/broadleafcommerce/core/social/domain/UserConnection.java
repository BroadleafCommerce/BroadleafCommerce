/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.social.domain;


public interface UserConnection {

    UserConnectionImpl.UserConnectionPK getUserConnectionPK();

    void setUserConnectionPK(UserConnectionImpl.UserConnectionPK userConnectionPK);

    Integer getRank();

    void setRank(Integer rank);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getProfileUrl();

    void setProfileUrl(String profileUrl);

    String getImageUrl();

    void setImageUrl(String imageUrl);

    String getAccessToken();

    void setAccessToken(String accessToken);

    String getSecret();

    void setSecret(String secret);

    String getRefreshToken();

    void setRefreshToken(String refreshToken);

    Long getExpireTime();

    void setExpireTime(Long expireTime);

}
