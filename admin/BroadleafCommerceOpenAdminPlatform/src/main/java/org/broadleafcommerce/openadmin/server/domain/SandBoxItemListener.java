package org.broadleafcommerce.openadmin.server.domain;

/**
 * Created by bpolster.
 */
public interface SandBoxItemListener {
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox);

    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox);

    public void itemReverted(SandBoxItem sandBoxItem);
}
