package com.buckaroos.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.buckaroos.server.Account;
import com.buckaroos.server.AccountTransaction;
import com.buckaroos.server.User;
import com.buckaroos.utility.CurrencyInformationProvider;
import com.buckaroos.utility.Money;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This class implements the ControllerInterface interface. This controller
 * object acts as a controller in the Model-View-Controller model. Controls the
 * communication between activities and User/Account.
 * 
 * @author Gene Hynson
 * @author Daniel Carnauba
 * @version 1.0
 */
public class UserAccountController implements ControllerInterface {

    private static User user = new User(" ", " ", " ");
    private static Account currentAccount;
    private static List<Account> userAccounts = new ArrayList<Account>();
    private static List<AccountTransaction> userTransactions;
    private static Map<String, Double> reportTransactions;
    private static List<String> reportTransactionNames;
    
    private String aPassword;
    private String aUsername;
    private String aEmail;
    private CurrencyInformationProvider currency;
    private List<String> currencies;
    private Money Money;
    
    private static AccountOverview accountOverview;
    private static Reports reports;
    private static ChangeAccount changeAccount;
    private static CreateAccount createAccount;
    private static Login login;
    private static Register register;  
    private static Transaction transaction;
    
    private static String beginDate;
    private static String theDate;
    private static String endDate;
    
    private static DBConnectionAsync db;
    private AsyncCallback<User> callbackUser;
    private AsyncCallback<Account> callbackAccount;
    private AsyncCallback<AccountTransaction> callbackTransaction;
    private AsyncCallback<List<AccountTransaction>> callbackListTransactions;
    private AsyncCallback<ArrayList<Account>> callbackListAccounts;
    private AsyncCallback<HashMap<String, Double>> callbackHashMap;
    private AsyncCallback callbackVoid;

    private boolean createChangeAccount = false;
    private boolean loginUser = false;
    private boolean registerUser = false;
    private boolean createAccountOverview = false;
    private boolean createReports = false;
    private boolean createCreateAccount = false;
    private boolean updateCurrentUser = false;
    private boolean doesLoginAccountExist = false;
    private boolean addAccount = false;
    private boolean addTransaction = false;
    private boolean changeDates = false;
    private boolean deleteTransaction = false;
    private boolean sendingResetPasswordEmail = false;
    private boolean sendingWelcomeEmail = false;
    private boolean updateUser = false;
    private boolean updateCurrentAccount = false;
    /**
     * Gets user/DB after login from CredientialConfirmer in Login activity.
     * 
     * @param user
     */
    @SuppressWarnings("static-access")
    public UserAccountController(User user) {
        this.user = user;
    	callbackUser = new CallbackHandler<User>();
    	callbackAccount = new CallbackHandler<Account>();
    	callbackTransaction = new CallbackHandler<AccountTransaction>();
    	callbackListTransactions = new CallbackHandler<List<AccountTransaction>>();
    	callbackListAccounts = new CallbackHandler<ArrayList<Account>>();
    	callbackHashMap = new CallbackHandler<HashMap<String, Double>>();
    	callbackVoid = new CallbackHandler();

    }

    public UserAccountController() {
    	callbackUser = new CallbackHandler<User>();
    	callbackAccount = new CallbackHandler<Account>();
    	callbackTransaction = new CallbackHandler<AccountTransaction>();
    	callbackListTransactions = new CallbackHandler<List<AccountTransaction>>();
    	callbackListAccounts = new CallbackHandler<ArrayList<Account>>();
    	callbackHashMap = new CallbackHandler<HashMap<String, Double>>();
    	callbackVoid = new CallbackHandler();

    }
    
    public UserAccountController(String url) {
    	callbackUser = new CallbackHandler<User>();
    	callbackAccount = new CallbackHandler<Account>();
    	callbackTransaction = new CallbackHandler<AccountTransaction>();
    	callbackListTransactions = new CallbackHandler<List<AccountTransaction>>();
    	callbackListAccounts = new CallbackHandler<ArrayList<Account>>();
    	callbackHashMap = new CallbackHandler<HashMap<String, Double>>();
    	callbackVoid = new CallbackHandler();

    	
    	reportTransactions = new HashMap<String, Double>();
    	reportTransactionNames = new ArrayList<String>();
    	
    	this.db = GWT.create(DBConnection.class);
    	ServiceDefTarget target = (ServiceDefTarget) this.db;
    	System.out.println(url);
    	target.setServiceEntryPoint(url);
    	
    	beginDate = new String();
    	endDate = new String();
    	theDate = new String();
    	
    }
    
//===============================
    //Add things
//===============================

    @Override
    public void addAccount(String accountName, String accountNickName,
            double amount, double interestRate) {
        currentAccount =
                new Account(user.getUsername(), accountName, accountNickName, amount, interestRate);
        addAccount = true;
        db.addAccount(user.getUsername(), currentAccount, callbackAccount);
    }

    @Override
    public void addWithdrawal(double amount, String currencyType,
            String category, Date date) {
        String dateString = convertDateToString(date);
        String timeString = convertTimeToString(date);
        addTransaction = true;
        db.addTransaction(user.getUsername(), currentAccount.getName(), amount,
                "Withdrawal", currencyType, category, dateString, timeString, callbackTransaction);
    }

    @Override
    public void addDeposit(double amount, String currencyType, String category,
            Date date) {
        String dateString = convertDateToString(date);
        String timeString = convertTimeToString(date);
        addTransaction = true;
        db.addTransaction(user.getUsername(), currentAccount.getName(), amount,
                "Deposit", currencyType, category, dateString, timeString, callbackTransaction);
    }
    
    /**
     * Stores a new account and password that has been registered. If the
     * account name has been taken, the user is informed
     * 
     * @param accountName The account name to be created
     * @param password The password that corresponds with the new account
     */
    private void storeAccount(String accountName, String password, String email) {
    	User newUser = new User(accountName, password, email);
    	if (accountName != null && email != null && password != null) {
//            MessageDigest md;
//            try {
//                md = MessageDigest.getInstance("MD5");
//                md.update(password.getBytes());
//                byte[] digest = md.digest();
//                StringBuffer sb = new StringBuffer();
//                for (byte b : digest) {
//                    sb.append(Integer.toHexString((int) (b & 0xff)));
//                }
//                newUser = new User(accountName, sb.toString(), email);
    		user = newUser;
    		db.addUser(newUser, callbackUser);
//            } catch (NoSuchAlgorithmException e1) {
//                // Do Nothing
//            }
    	}
    }
    
    @Override
    public void updateUser(String username, String password, String email) {
    	user.setAccountName(username);
    	user.setEmail(email);
    	user.setPassword(password);
    	db.updateUser(username, password, email, callbackUser);
    	updateUser = true;
    }
    
//=============================
    //convert things
//=============================

    @Override
    public String convertTimeToString(Date date) {
    	String pattern = "HH:mm"; /*your pattern here*/ 
    	DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
    	DateTimeFormat df = new DateTimeFormat(pattern, info) {};  // <= trick here
        return df.format(date);
    }

    @Override
    public String convertDateToString(Date date) {
    	String pattern = "yyyy/MM/dd"; /*your pattern here*/ 
    	DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
    	DateTimeFormat df = new DateTimeFormat(pattern, info) {};  // <= trick here
        return df.format(date);
    }

    @Override
    public Date convertStringToDate(String dateString) {
    	String pattern = "yyyy/MM/dd"; /*your pattern here*/ 
    	DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
    	DateTimeFormat df = new DateTimeFormat(pattern, info) {};  // <= trick here
        return df.parse(dateString);
    }
    
//=============================
    //check things
//=============================
    
    private void doesLoginAccountExist(String username) {
    	db.getUser(username, callbackUser);
    }
    
    private boolean doesAccountExist(String accountName) {
    	for (Account acc : userAccounts) {
			if (acc.getName().equalsIgnoreCase(accountName)) {
				return true;
			}
		}
    	return false;
    }
    
    @Override
    public boolean hasAccount() {
    	if (userAccounts != null && !userAccounts.isEmpty()) {
    		return true;
    	}
    	return false;
    }
    
    private boolean isPasswordCorrect(String username, String aPassword, String thePassword) {
    	StringBuffer sb = null;
    	System.out.println("TheAccount: " + username);
    	System.out.println("ThePassword: " + thePassword);
//        MessageDigest md;
//        sb = new StringBuffer();
//        sb.append("");
//        try {
//            md = MessageDigest.getInstance("MD5");
//            md.update(aPassword.getBytes());
//            byte[] digest = md.digest();
//            sb.replace(0, 0, "");
//            for (byte b : digest) {
//                sb.append(Integer.toHexString((int) (b & 0xff)));
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        System.out.println("aPassword: " + sb.toString());
//        if (thePassword.equals(sb.toString())) {
    	if (thePassword.equals(aPassword)) {
    		return true;
    	}
    	user = null;
    	return false;
    }
    
//============================
    //set things
//============================
    
    public void setCurrentUser(User user) {
    	UserAccountController.user = user;
    }
    
    /**
     * Sets the begin date.
     * 
     * @param beginDate The beginDate to set.
     */
    public static void setBeginDate(String beginDate) {
    	UserAccountController.beginDate = beginDate;
    }
    
    /**
     * Sets the calendar date.
     * 
     * @param theDate The calendar date to set for a time period.
     */
    public static void setTheDate(String theDate) {
    	UserAccountController.theDate = theDate;
    }
    
    @Override
    public void setCurrentAccount(Account account) {
    	currentAccount = account;
    }
    
    /**
     * Sets the end date for a time period.
     * 
     * @param endDate The endDate to set for a time period.
     */
    public static void setEndDate(String endDate) {
    	UserAccountController.endDate = endDate;
    }
    
    
//=============================
    //get things
//=============================

    @Override
    public Account getCurrentAccount() {
        return currentAccount;
    }
    
    @Override
    public Account getUserAccount(String accountName) {
    	for (Account acc : userAccounts) {
    		if (acc.getName().equalsIgnoreCase(accountName)) {
    			return acc;
    		}
    	}
    	return null;
    }


    @Override
    public Account getFirstUserAccount() {
        return userAccounts.get(0);
    }

    @Override
    public DBConnectionAsync getDB() {
        return db;
    }


    @Override
    public void getLoginAccount(String username) {
    	db.getUser(username, callbackUser);
    }

    @Override
    public User getCurrentUser() {
        return user;
    }
    
    /**
     * Gets the begin date.
     * 
     * @return The beginDate.
     */
    public static String getBeginDate() {
    	return beginDate;
    }
    
    /**
     * Gets the calendar date object.
     * 
     * @return The calendar date object.
     */
    public static String getTheDate() {
    	return theDate;
    }
    
    /**
     * Gets the endDate for a time period.
     * 
     * @return The endDate. The end date for a time period.
     */
    public static String getEndDate() {
    	return endDate;
    }
    @Override
    public List<String> getCurrencyFullNames() {
    	currencies = new ArrayList<String>();
    	currency = new CurrencyInformationProvider();
    	currencies.add(currency.getFullCurrencyName(Money.USD));
    	currencies.add(currency.getFullCurrencyName(Money.EUR));
    	currencies.add(currency.getFullCurrencyName(Money.GBP));
    	currencies.add(currency.getFullCurrencyName(Money.CAD));
    	currencies.add(currency.getFullCurrencyName(Money.AUD));
    	currencies.add(currency.getFullCurrencyName(Money.JPY));
    	currencies.add(currency.getFullCurrencyName(Money.INR));
    	currencies.add(currency.getFullCurrencyName(Money.CHF));
    	currencies.add(currency.getFullCurrencyName(Money.RUB));
    	currencies.add(currency.getFullCurrencyName(Money.BRL));
    	currencies.add(currency.getFullCurrencyName(Money.MXN));
    	currencies.add(currency.getFullCurrencyName(Money.CNY));
    	currencies.add(currency.getFullCurrencyName(Money.AED));
    	currencies.add(currency.getFullCurrencyName(Money.BDT));
    	return currencies;
    }
    
    @Override
    public List<String> getCurrencySymbols() {
    	currencies = new ArrayList<String>();
    	currency = new CurrencyInformationProvider();
    	currencies.add(currency.getSymbolOfCurrency(Money.USD));
    	currencies.add(currency.getSymbolOfCurrency(Money.EUR));
    	currencies.add(currency.getSymbolOfCurrency(Money.GBP));
    	currencies.add(currency.getSymbolOfCurrency(Money.CAD));
    	currencies.add(currency.getSymbolOfCurrency(Money.AUD));
    	currencies.add(currency.getSymbolOfCurrency(Money.JPY));
    	currencies.add(currency.getSymbolOfCurrency(Money.INR));
    	currencies.add(currency.getSymbolOfCurrency(Money.CHF));
    	currencies.add(currency.getSymbolOfCurrency(Money.RUB));
    	currencies.add(currency.getSymbolOfCurrency(Money.BRL));
    	currencies.add(currency.getSymbolOfCurrency(Money.MXN));
    	currencies.add(currency.getSymbolOfCurrency(Money.CNY));
    	currencies.add(currency.getSymbolOfCurrency(Money.AED));
    	currencies.add(currency.getSymbolOfCurrency(Money.BDT));
    	return currencies;
    }

//==========================
    //generate things
//==========================

    @Override
    public void generateSpendingCategoryReport() {
    	db.getSpendingCategoryInfo(user.getUsername(),
    			currentAccount.getName(), beginDate,
    			endDate, callbackHashMap);
    }
    
    @Override
    public void generateIncomeSourceReport() {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void generateCashFlowReport() {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void generateAccountListingReport() {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void generateTransactionHistoryReport() {
    	// TODO Auto-generated method stub
    }
    
    @Override
    public void resetPassword(String username) {
    	sendingResetPasswordEmail = true;
    	db.getUser(username, callbackUser);
    }
    
    @Override
    public void welcomeEmail(String username) {
    	sendingWelcomeEmail = true;
    	db.getUser(username, callbackUser);
    }

//===========================
    //handle things
//===========================
    
    private class CallbackHandler<T> implements AsyncCallback<T> {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Server request failed.");
		}

		@Override
		public void onSuccess(T result) {
			if (createChangeAccount) {
				userAccounts = (List<Account>) result;
				changeAccount = new ChangeAccount(userAccounts);
				createChangeAccount = false;
			} else if (createAccountOverview) {
				userTransactions = (List<AccountTransaction>) result;
				accountOverview = new AccountOverview(userTransactions);
				createAccountOverview = false;
			} else if (createReports) {
				reportTransactions = (Map<String, Double>) result;
				reportTransactionNames.addAll(reportTransactions.keySet());
				reports = new Reports(reportTransactions, reportTransactionNames);
				createReports = false;
			} else if (loginUser) {
				user = (User) result;
				if (isPasswordCorrect(user.getUsername(), aPassword, user.getPassword())) {
					RootPanel.get("page").clear();
					createChangeAccount();
				} else {
					login.displayNotCorrect();
				}
				loginUser = false;
			} else if (registerUser) {
				RootPanel.get("page").clear();
				createCreateAccount();
				registerUser = false;
			} else if (updateCurrentUser) {
				user = (User) result;
				updateCurrentUser = false;
			} else if (updateCurrentAccount) {
				currentAccount = (Account) result;
				updateCurrentAccount = false;
				db.getAllTransactions(user.getUsername(), currentAccount.getName(), callbackListTransactions);
				createAccountOverview = true;
			} else if (doesLoginAccountExist) {
				if (result == null) {
					registerUser = true;
					storeAccount(aUsername, aPassword, aEmail);
					welcomeEmail(user.getUsername());
				} else {
					register.displayAccountAlreadyExists();
				}
				doesLoginAccountExist = false;
			} else if (createCreateAccount) {
				userAccounts = (List<Account>) result;
				createCreateAccount = false;
			} else if (addAccount) {
				if (doesAccountExist(currentAccount.getName())) {
					createAccount.displayAccountAlreadyExists();
				} else {
					RootPanel.get("page").clear();
					createChangeAccount();
				}
				addAccount = false;
			} else if (addTransaction) {
				RootPanel.get("page").clear();
				createAccountOverview();
				addTransaction = false;
			} else if (changeDates) {
				reportTransactions = (Map<String, Double>) result;
				reportTransactionNames.addAll(reportTransactions.keySet());
				System.out.println(reportTransactions.get("food"));
				System.out.println(reportTransactions.size());
				reports.setTransactionLists(reportTransactions, reportTransactionNames);
				changeDates = false;
			} else if (deleteTransaction) {
				RootPanel.get("page").clear();
				createAccountOverview();
				deleteTransaction = false;
			} else if (sendingResetPasswordEmail) {
				user = (User) result;
				if (user != null) {
					db.sendResetPassword(user.getEmail(), user.getPassword(), user.getUsername(), callbackVoid);
					System.out.println("Not null...email should have sent");
					Window.alert("Email sent.");
				} else {
					System.out.println("User is null");
					Window.alert("Not valid username. Please enter valid username and try again.");
				}
				sendingResetPasswordEmail = false;
			} else if (updateUser) {
				RootPanel.get("page").clear();
				createChangeAccount();
				updateUser = false;
				//do nothing
			} else if (sendingWelcomeEmail) {
				user = (User) result;
				db.sendWelcomeEmail(user.getEmail(), user.getEmail(), callbackVoid);
				sendingWelcomeEmail = false;
			} else if (result == null) {
				System.out.println("Result is currently null");
			}
    	
		}
    }

	public void createChangeAccount() {
		createChangeAccount = true;
        db.getAllAccounts(user.getUsername(), callbackListAccounts);
	}
	
	public void createAccountOverview() {
		updateCurrentAccount = true;
        db.getAccount(user.getUsername(), currentAccount.getName(), callbackAccount);
	}
	
	public void createReports() {
		createReports = true;
		generateSpendingCategoryReport();
	}
	
	public void createRegister() {
		register = new Register();
	}
	
	public void createLogin() {
		login = new Login();
	}
	
	public void createCreateAccount() {
		createCreateAccount = true;
		db.getAllAccounts(user.getUsername(), callbackListAccounts);
		createAccount = new CreateAccount();
	}
	
	public void loginUser(String accountName, String password) {
		loginUser = true;
		aPassword = password;
		db.getUser(accountName, callbackUser);
	}
	
	public void registerUser(String name, String password, String email) {
		doesLoginAccountExist = true;
		aPassword = password;
		aUsername = name;
		aEmail = email;
    	doesLoginAccountExist(name);
	}
	
	public void changeDates(String beforeDate, String afterDate) {
		changeDates = true;
		beginDate = beforeDate;
		endDate = afterDate;
		generateSpendingCategoryReport();
	}
	
	public void editTransaction(AccountTransaction t) {
		RootPanel.get("page").clear();
		transaction = new Transaction();
		Date d = convertStringToDate(t.getDate());
		String time = d.getHours() + ":" + d.getMinutes();
		transaction.setValues(t.getType(), t.getAmount(), t.getCategory(), d, time);
		deleteTransaction(t);
	}
	
	public void deleteTransaction(AccountTransaction t) {
		deleteTransaction = true;
		db.deleteTransaction(user.getUsername(), currentAccount.getName(), t.getAmount(), t.getCategory(), t.getTime(), t.getDate(), callbackTransaction);
	}
	
}