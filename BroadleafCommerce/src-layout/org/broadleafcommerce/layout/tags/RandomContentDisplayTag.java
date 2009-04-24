package org.broadleafcommerce.layout.tags;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.servlet.jsp.JspException;

import org.apache.commons.beanutils.BeanComparator;
import org.broadleafcommerce.marketing.domain.TargetContent;

public class RandomContentDisplayTag extends ContentDisplayTag {
    private static final long serialVersionUID = 1L;

    private int numItems;



    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException{
        super.doStartTag();
        List<TargetContent> targetContents = (List<TargetContent>)pageContext.getAttribute(getVarList());
        List<TargetContent> randomizedContents = randomizeSublist(targetContents, targetContents.size());
        Collections.sort(randomizedContents, new BeanComparator("priority"));
        pageContext.setAttribute(getVarList(), randomizedContents);
        pageContext.setAttribute(getVarFirstItem(), (randomizedContents.size() > 0)?randomizedContents.get(0):null);
        return EVAL_PAGE;
    }

    public List<TargetContent> randomizeSublist(List<TargetContent> list, int index) {
        if(index > 1) {
            int size = index;
            Random random = new Random();

            for(int i = 0; i <= size; i++) {
                int j = random.nextInt(size);
                TargetContent obj = list.get(i);

                list.set(i, list.get(i + j));
                list.set(i + j, obj);

                size--;
            }
        }
        return list;
    }
}
