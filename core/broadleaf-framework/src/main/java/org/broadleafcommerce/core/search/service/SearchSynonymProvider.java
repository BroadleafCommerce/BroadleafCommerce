///*
// * Copyright 2008-2009 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.broadleafcommerce.core.search.service;
//
//import java.util.HashMap;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.apache.lucene.analysis.TokenStream;
//import org.broadleafcommerce.core.search.dao.SearchSynonymDao;
//import org.broadleafcommerce.core.search.domain.SearchSynonym;
//import org.broadleafcommerce.core.util.ApplicationContextHolder;
//import org.compass.core.CompassException;
//import org.compass.core.config.CompassSettings;
//import org.compass.core.lucene.engine.analyzer.LuceneAnalyzerTokenFilterProvider;
//import org.compass.core.lucene.engine.analyzer.synonym.SynonymFilter;
//import org.compass.core.lucene.engine.analyzer.synonym.SynonymLookupProvider;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//@Component("blSearchSynonymProvider")
//public class SearchSynonymProvider implements SynonymLookupProvider, LuceneAnalyzerTokenFilterProvider {
//    
//    @Resource(name="blSearchSynonymDao")
//    private SearchSynonymDao searchSynonymDao;
//    
//    private HashMap<String, String[] > synonymMap;
//    
//    public String[] lookupSynonyms(String value) {
//        if (synonymMap == null) {
//            configure(null);
//        }
//        if (synonymMap == null) {
//            return null;
//        }
//        return synonymMap.get(value);
//    }
//
//    public void configure(CompassSettings settings) throws CompassException {
//        if (searchSynonymDao == null) {
//	        ApplicationContext context = ApplicationContextHolder.getApplicationContext();
//	        if (context == null) return;
//	        searchSynonymDao = (SearchSynonymDao)context.getBean("blSearchSynonymDao");
//        }
//        synonymMap = new HashMap<String, String[]>();
//        List<SearchSynonym> synonyms = searchSynonymDao.getAllSynonyms();
//        synonymMap.clear();
//        for (SearchSynonym synonym : synonyms) {
//            synonymMap.put(synonym.getTerm(), synonym.getSynonyms());
//        }
//    }
//
//    public TokenStream createTokenFilter(TokenStream tokenStream) {
//        return new SynonymFilter(tokenStream, this);
//    }
//    
//}
