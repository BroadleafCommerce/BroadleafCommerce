<%@ include file="/WEB-INF/jsp/include.jsp"%>
    <!--
    <tcs:customerInfoTag var="customer"/>   

    <tcs:customerPhoneTag var="phoneNoToDelete" customerPhoneId="123"/>

    <tcs:customerPhoneTag var="allCustomerPhoneNums"/>
    -->
    
        <h1 style="margin-bottom: 11px;">Your Phones</h1>
        
        <c:out value="******************************* ${confirmationMessage}"/>
                
        <!--
        <c:if test="${!empty customerPhones}">

             Existing Phones 
            <h3>Edit or Delete a Phone</h3>
            <tags:grid columnCount="3" collection="${customerPhones}" cellClass="addressCell">
                <c:set var="personFormId" value="person${status.index}" />
                <c:url var="deleteUrl" value="/myaccount/phoneDeleteConfirmation.htm" />
                <strong>${item.phoneName}</strong>
                <br />${item.phone.phoneNumber}&nbsp;
                <c:choose>
                    <c:when test="${item.phone.default}">
                        <span class="instructions">(Default Phone)</span>
                        <br />
                    </c:when>
                    <c:otherwise>
                        <c:url value="/myaccount/phoneDefault.htm" var="makeDefaultUrl">
                            <c:param name="customerPhoneId" value="${item.id}" />
                        </c:url>
                        <span class="instructions">(<a href="${makeDefaultUrl}">Make Default</a>)</span>
                        <br />
                    </c:otherwise>
                </c:choose>
                <input type="hidden" name="customerPhoneId" value="${item.id}" />
                <form method="post" action="/myaccount/phones.htm" style="display: inline;">
                    <input type="hidden" name="customerPhoneId" value="${item.id}">
                    <input type="hidden" name="action" value="edit">
                    <input type="submit" class="submitButton smallButton" value="Edit" name="submitAction" />
                </form>
                <form method="post" action="/myaccount/phoneDeleteConfirmation.htm" style="display: inline;">
                    <input type="hidden" name="customerPhoneId" value="${item.id}">
                    <input type="submit" class="submitButton smallButton" value="Delete" name="submitAction" />
                </form>
            </tags:grid>

        </c:if>

        -->
        
        <form:form modelAttribute="phoneNameForm" action="/broadleafdemo/myaccount/phone/savePhone.htm">
        
            <input type="hidden" name="customerPhoneId" value="${customerPhoneId}">
            <input type="hidden" name="phoneId" value="${phoneId}">
            <c:choose>
                <c:when test="${customerPhoneId ne NULL}">
                    <br /><h3>Edit an existing Phone&nbsp;&nbsp;<span class="instructions">* required field</span></h3>
                </c:when>
                <c:otherwise>
                    <br /><h3>Add a Phone&nbsp;&nbsp;<span class="instructions">* required field</span></h3>
                </c:otherwise>
            </c:choose>

            <!-- Form Errors -->
            <div style="color: #CC0000; font-weight: bold;">
            <p><form:errors path="*" /></p>
            </div>

            <table width="100%" border="0" cellspacing="3" cellpadding="0">
                <tr>
                    <td width="90">Phone Number *</td>
                    <td width="160">
                        <form:input path="phone.phoneNumber" size="20" maxlength="30" />
                    </td>
                </tr>
                <tr>
                    <td align="left">Phone Nickname ${phoneName3 }</td>
                    <td colspan="3">
                        <form:input path="phoneName" size="20" maxlength="30" />
                        <c:if test="${phoneNameForm.phone.default eq true}">
                            <c:out value="This phone is your default phone."/>
                        </c:if>
                        <br/>
                    </td>
                </tr>
                <tr>
                    <td>
                    </td>
                    <td>
                        Active
                        <form:radiobutton id="phone.active" path="phone.active" value="true" />
                    </td>
                    <td>
                        Inactive
                        <form:radiobutton id="phone.active" path="phone.active" value="false" />
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td colspan="3"><span class="instructions">(ex. Home, Work)</span></td>
                </tr>
                <tr>
                    <td colspan="4"><img src="/images/etc/dot_clear.gif" width="1" height="1" vspace="3" border="0"></td>
                </tr>
                <tr>
                    
                    <td>
                        <a href="/broadleafdemo/myaccount/phone/viewPhone.htm" class="submitLink" style="margin-right: 12px;">
                            Cancel
                        </a>  
                        
                        <c:if test="${customerPhoneId ne NULL}">
                            <a href="/broadleafdemo/myaccount/phone/deletePhone.htm?customerPhoneId=${customerPhoneId}" class="submitLink" style="margin-right: 12px;">
                                Delete
                            </a>  
                            <c:if test="${phoneNameForm.phone.default eq false}">
                                <a href="/broadleafdemo/myaccount/phone/makePhoneDefault.htm?customerPhoneId=${customerPhoneId}" class="submitLink" style="margin-right: 12px;">
                                    Set As Default
                                </a>
                            </c:if>  
                        </c:if>
                        
                        <input type="submit" class="submitButton" value="Save" name="savePhone" />
                    </td>
                </tr>
            </table>
        </form:form>

