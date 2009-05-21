package org.broadleafcommerce.search.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;
import org.broadleafcommerce.catalog.domain.Sku;

public interface SearchService {

    public void rebuildSkuIndex() throws CorruptIndexException, LockObtainFailedException, IOException;

    public List<Sku> performSearch(String input) throws CorruptIndexException, IOException, ParseException;
}
