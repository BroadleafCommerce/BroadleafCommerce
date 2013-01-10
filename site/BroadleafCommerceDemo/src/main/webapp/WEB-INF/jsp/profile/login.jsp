<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tiles:insertDefinition name="baseNoSide">
    <tiles:putAttribute name="pageHeadContent">
        <link rel="stylesheet" type="text/css" href="/styles/profile.css" />
    </tiles:putAttribute>
    <tiles:putAttribute name="mainContent" type="string">
        <h1>My Account - Sign In</h1>
        <tags:columns>
            <div class="column" style="width:295px;border-right:1px solid #ccc;margin-right:15px;">
                <p>
                    <c:choose>
                        <c:when test="${empty param.currentProcess}">
                            Please enter your email address and password to access your account.
                        </c:when>
                        <c:otherwise>
                            <c:if test="${param.currentProcess eq 'viewProject'}">
                                Please enter your email address and password to access your project.
                            </c:if>
                            <c:if test="${param.currentProcess eq 'sendPassword'}">
                                <div class="alert">Please enter the temporary password that was emailed to you to sign in to your account.</div>                            </c:if>
                            <c:if test="${param.currentProcess eq 'resetPassword'}">
                                <div class="alert">Your password has been successfully changed!  Please enter your new password to sign in to your account.</div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </p>
                <c:if test="${not empty param.login_error}">
                    <p class="error"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></p>
                </c:if>
                <form id="form.login" name="f" action="<c:url value="/loginProcess" />" method="post">
                    <table class="loginInfo">
                        <tr>
                            <td style="text-align:right"><label for="j_username">Email:</label></td>
                            <td><input size="30" class="loginField initFocus" type="text" name="j_username" id="j_username" <c:if test="${not empty param.login_error}">value="${sessionScope["SPRING_SECURITY_LAST_USERNAME"]}"</c:if> /></td>
                        </tr>
                        <tr>
                            <td style="text-align:right"><label for="j_password">Password:</label></td>
                            <td><input size="30" class="loginField" type="password" name="j_password" id="j_password" /></td>
                        </tr>
                        <tr>
                            <td style="text-align:right"><label for="_spring_security_remember_me">Remember Me:</label></td>
                            <td><input type="checkbox" name="_spring_security_remember_me" id="_spring_security_remember_me" /></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td class="instructions">Note: Password is case sensitive</td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td style="text-align:right;" class="loginButton">
                                <input name="successUrl" id="submit" type="hidden" value="${param.successUrl}" />
                                <input class="submitButton" name="submit" id="submit" type="submit" value="Sign In" />
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <div class="column" style="width:300px;">
                <p><span class="boldText">Not registered yet?</span>
                    <br /><a id="link.registerUser" href="/profile/registerUser.htm">Click Here</a> to join <span style="color:#333399;font-weight:bold;">The Neatest Site on the Web!<sup>SM</sup></span>
                </p>
                <p><span class="boldText">Forgot your password?</span>
                    <br/><a id="link.forgotPassword" href="/profile/forgotPassword.htm">Click Here</a> to have a new one sent to you.
                </p>
            </div>
        </tags:columns>
    </tiles:putAttribute>
</tiles:insertDefinition>