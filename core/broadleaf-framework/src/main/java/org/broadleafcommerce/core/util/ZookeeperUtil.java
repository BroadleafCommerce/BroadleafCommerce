/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.broadleafcommerce.common.util.GenericOperation;
import org.broadleafcommerce.common.util.GenericOperationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple utility to assist in basic operations related to Zookeeper.
 * 
 * @author Kelly Tisdell
 *
 */
public class ZookeeperUtil {
    
    /**
     * Recursively deletes a path in Zookeeper.  For example, if you have a path like /path/to/my/element, then passing "/path" to this method will delete the 
     * directory tree recursively.
     * 
     * @param path
     * @param zk
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void deleteRecursive(final String path, final ZooKeeper zk) throws KeeperException, InterruptedException {
        ZKUtil.deleteRecursive(zk, path);
    }

    /**
     * Creates a path in Zookeeper, if it does not already exist, with CreateMode.PERSISTENT for the create mode and ZooDefs.Ids.OPEN_ACL_UNSAFE for ACLs.
     * If any part of the path exists, then the rest of this path will be appended to it..
     * 
     * @param path
     * @param zk
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void makePath(String path, final ZooKeeper zk) throws KeeperException, InterruptedException {
        makePath(path, null, zk, CreateMode.PERSISTENT);
    }
    
    public static void makePath(String path, final byte[] data, final ZooKeeper zk) throws KeeperException, InterruptedException {
        makePath(path, data, zk, CreateMode.PERSISTENT);
    }
    
    public static void makePath(String path, final byte[] data, final ZooKeeper zk, CreateMode createMode) throws KeeperException, InterruptedException {
        makePath(path, data, zk, createMode, ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }
    
    /**
     * Creates a path in Zookeeper, specified by the path argument.  For example: /path/to/my/element.  If part or all of the path already exists, then it will not be 
     * created.  If the data byte array is not null and has a size greater than 0, then it will be added to the final node of the path.  If the final node 
     * of the path already exists, then the data will be ignored.
     * 
     * If the ACLs are null or empty, then ZooDefs.Ids.OPEN_ACL_UNSAFE will be used.
     * 
     * If createMode is null, then CreateMode.PERSISTENT will be used.
     * 
     * @param path
     * @param data
     * @param zk
     * @param createMode
     * @param acls
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void makePath(String path, final byte[] data, final ZooKeeper zk, CreateMode createMode, final List<ACL> acls) throws KeeperException, InterruptedException {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        final String[] tokens = path.split("/");
        final List<Op> ops = new ArrayList<>();
        
        final CreateMode mode;
        if (createMode == null) {
            mode = CreateMode.PERSISTENT;
        } else {
            mode = createMode;
        }
        
        final List<ACL> acl;
        if (acls != null && ! acls.isEmpty()) {
            acl = acls;
        } else {
            acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
        
        try {
            final AtomicInteger tokenCount = new AtomicInteger(1);
            final StringBuilder sb = new StringBuilder();
            for (final String token : tokens) {
                sb.append('/').append(token);
                if (zk.exists(sb.toString(), false) == null) {
                    if (tokenCount.get() == tokens.length && data != null && data.length > 0) {
                        ops.add(Op.create(sb.toString(), data, acl, mode));
                    } else {
                        ops.add(Op.create(sb.toString(), null, acl, mode));
                    }
                    
                }
                tokenCount.incrementAndGet();
            }
            GenericOperationUtil.executeRetryableOperation(new GenericOperation<Void>() {
                @Override
                public Void execute() throws Exception {
                    zk.multi(ops);
                    return null;
                }
            });
        } catch (KeeperException | InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occured creating the path, " + path + ", in Zookeeper.", e);
        }
    }
    
    public static boolean exists(final String path, final ZooKeeper zk) throws KeeperException, InterruptedException {
        try {
            return GenericOperationUtil.executeRetryableOperation(new GenericOperation<Boolean>() {
                @Override
                public Boolean execute() throws Exception {
                    return zk.exists(path, false) != null;
                }
            });
        } catch (KeeperException | InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An error occured determining if " + path + " exists in Zookeeper.", e);
        }
    }
    
}
