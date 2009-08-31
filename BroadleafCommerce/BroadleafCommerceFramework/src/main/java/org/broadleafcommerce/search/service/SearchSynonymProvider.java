package org.broadleafcommerce.search.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.analysis.TokenStream;
import org.broadleafcommerce.search.dao.SearchSynonymDao;
import org.broadleafcommerce.search.domain.SearchSynonym;
import org.broadleafcommerce.util.ApplicationContextHolder;
import org.compass.core.CompassException;
import org.compass.core.config.CompassSettings;
import org.compass.core.lucene.engine.analyzer.LuceneAnalyzerTokenFilterProvider;
import org.compass.core.lucene.engine.analyzer.synonym.SynonymFilter;
import org.compass.core.lucene.engine.analyzer.synonym.SynonymLookupProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component("blSearchSynonymProvider")
public class SearchSynonymProvider implements SynonymLookupProvider, LuceneAnalyzerTokenFilterProvider {
    
    @Resource(name="blSearchSynonymDao")
    private SearchSynonymDao searchSynonymDao;
    
    private HashMap<String, String[] > synonymMap;
    
    public String[] lookupSynonyms(String value) {
        if (synonymMap == null) {
            configure(null);
        }
        if (synonymMap == null) {
            return null;
        }
        return synonymMap.get(value);
    }

    public void configure(CompassSettings settings) throws CompassException {
        if (searchSynonymDao == null) {
	        ApplicationContext context = ApplicationContextHolder.getApplicationContext();
	        if (context == null) return;
	        searchSynonymDao = (SearchSynonymDao)context.getBean("blSearchSynonymDao");
        }
        synonymMap = new HashMap<String, String[]>();
        List<SearchSynonym> synonyms = searchSynonymDao.getAllSynonyms();
        synonymMap.clear();
        for (SearchSynonym synonym : synonyms) {
            synonymMap.put(synonym.getTerm(), synonym.getSynonyms());
        }
    }

    public TokenStream createTokenFilter(TokenStream tokenStream) {
        return new SynonymFilter(tokenStream, this);
    }
    
}
