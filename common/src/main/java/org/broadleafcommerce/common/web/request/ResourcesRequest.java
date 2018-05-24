package org.broadleafcommerce.common.web.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Keeps track of the resources needed for &lt;blc:bundle&gt; and &lt;blc:bundlepreload&gt; tags so that the list
 * of files does not need to be duplicated across both tags.
 * <p>
 * If bundling is enabled and appropriate, use the {@link #getBundleForBundleName(String)} and
 * {@link #saveBundleForBundleName(String, String)} to fetch and store the bundled file for the bundle name.
 * <p>
 * If not using bundling, use {@link #getFilesForBundleName(String)} and {@link #saveFilesForBundleName(String, List)}
 * to fetch and store the files associated with the bundle name.
 *
 * @author Jacob Mitash
 */
@RequestScope
@Component("blResourcesRequest")
public class ResourcesRequest {

    protected Map<String, String> bundlesRequested = new HashMap<>();

    protected Map<String, List<String>> filesRequested = new HashMap<>();

    /**
     * Gets the bundle for the bundle name if previously used on this request
     * @param name the name of the bundle to search for
     * @return the bundle if found, otherwise null
     */
    public String getBundleForBundleName(String name) {
        return bundlesRequested.get(name);
    }

    /**
     * Saves the bundle with the given name to the request so it can be recalled later in the template
     * @param name the name of the bundle to save
     * @param bundle the path of the bundle
     */
    public void saveBundleForBundleName(String name, String bundle) {
        bundlesRequested.put(name, bundle);
    }

    /**
     * Gets the files included in a bundle if previously used on this request
     * @param name the name of the bundle to search for
     * @return the list of files in the bundle if found, otherwise null
     */
    public List<String> getFilesForBundleName(String name) {
        return filesRequested.get(name);
    }

    /**
     * Saves the bundle with the given name and files so it can be recalled later in the template
     * @param name the name of the bundle to save
     * @param files the files to include in the bundle
     */
    public void saveFilesForBundleName(String name, List<String> files) {
        filesRequested.put(name, files);
    }
}
