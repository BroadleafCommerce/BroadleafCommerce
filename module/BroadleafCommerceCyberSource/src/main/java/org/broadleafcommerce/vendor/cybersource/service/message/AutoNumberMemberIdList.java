package org.broadleafcommerce.vendor.cybersource.service.message;

import java.util.ArrayList;
import java.util.Collection;

public class AutoNumberMemberIdList extends ArrayList<CyberSourceItemRequest> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(CyberSourceItemRequest o) {
        boolean response = super.add(o);
        renumberIds();
        return response;
    }

    @Override
    public void add(int index, CyberSourceItemRequest element) {
        super.add(index, element);
        renumberIds();
    }

    @Override
    public boolean addAll(Collection<? extends CyberSourceItemRequest> c) {
        boolean response = super.addAll(c);
        renumberIds();
        return response;
    }

    @Override
    public boolean addAll(int index, Collection<? extends CyberSourceItemRequest> c) {
        boolean response = super.addAll(index, c);
        renumberIds();
        return response;
    }

    private void renumberIds() {
        long id = 0;
        for (CyberSourceItemRequest itemRequest : this) {
            itemRequest.setId(id);
            id++;
        }
    }

}
