package domain;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import domain.card.OwnableCard;
import domain.square.OwnableSquare;
import domain.square.TitleDeedSquare;
import domain.square.TitleDeedSquareColor;
import domain.util.Observable;

/**
 * Class that represents the real world players in the game.
 * 
 * @overview Contains Player's needed information and properties. Responsible
 *           for money transactions of player.
 * 
 * @author Team Pennybags
 */

public class Player extends Observable implements Serializable {

	private String nickName;
	private int totalMoney;
	private boolean isReverseDirection;
	private boolean inJail;
	private int jailTime;
	private List<OwnableCard> cards;
	private List<OwnableSquare> properties;
	private Token token;
	private String localIp;

	private static final int START_MONEY = 3200;

	/**
	 * @param nickName
	 *            Nick name of the player.
	 */
	public Player(String nickName) {
		this.nickName = nickName;
		this.totalMoney = START_MONEY;
		this.isReverseDirection = false;
		this.inJail = false;
		this.jailTime = 0;
		this.cards = new ArrayList<>();
		this.properties = new ArrayList<>();
		try {
			this.localIp = Inet4Address.getLocalHost().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	/**
	 * @return the localIp
	 */
	public String getLocalIp() {
		return localIp;
	}



	/**
	 * @param localIp the localIp to set
	 */
	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Player [nickName=" + nickName + ", totalMoney=" + totalMoney + ", isReverseDirection="
				+ isReverseDirection + ", inJail=" + inJail + ", jailTime=" + jailTime + ", cards=" + cards
				+ ", properties=" + properties + ", token=" + token + "]";
	}

	/**
	 * @return The nick name of the player.
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName
	 *            The nick name of the player to set.
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * @return The current money amount of the player.
	 */
	public int getTotalMoney() {
		return totalMoney;
	}

	/**
	 * Increase <b><tt>this</tt></b>' totalMoney as the given amount money.
	 * 
	 * 
	 * @param money
	 *            The amount to be added to player's total money.
	 * 
	 * @modifies <b><tt>this</tt></b>
	 * @effects changes the money amount of this
	 */
	public void increaseMoney(int money) {
		int oldVal = this.totalMoney;
		this.totalMoney += money;
		publishPropertyEvent("money", oldVal, this.totalMoney);
	}

	/**
	 * Decrease <b><tt>this</tt></b>' totalMoney as the given amount money if <b><tt>this</tt></b>' totalMoney is
	 * enough.
	 * 
	 * @param money
	 *            The amount to decrease from player's total money.
	 *
	 * @modifies <b><tt>this</tt></b>
	 * @effects if <b><tt>this</tt></b> has enough money, <b><tt>this</tt></b>' money amount is decreased.
	 * 
	 * @return <tt>true</tt> if player is capable of paying and has paid to money,
	 *         <tt>false</tt> otherwise.
	 */
	public boolean decreaseMoney(int money) {
		if (this.totalMoney < money) {
			return false;
		}
		int oldVal = this.totalMoney;
		this.totalMoney -= money;
		publishPropertyEvent("money", oldVal, this.totalMoney);

		return true;
	}

	/**
	 * 
	 * @param player
	 *            The player who will get the money.
	 * @param money
	 *            The amount to pay.
	 */
	public void payMoney(Player player, int money) {
		if (this.getTotalMoney() < money) {
			this.goBankrupt();
			return;
		}
		this.decreaseMoney(money);
		player.increaseMoney(money);
	}

	/**
	 * @return <tt>true</tt> if player is moving in reverse direction,
	 *         <tt>false</tt> otherwise.
	 */
	public boolean isReverseDirection() {
		return isReverseDirection;
	}

	/**
	 * @param isReverseDirection
	 *            Whether the player is moving in reverse direction or not.
	 */
	public void setReverseDirection(boolean isReverseDirection) {
		this.isReverseDirection = isReverseDirection;
	}

	/**
	 * @return <tt>true</tt> if player is in jail, <tt>false</tt> otherwise.
	 */
	public boolean isInJail() {
		return inJail;
	}

	/**
	 * Sets the inJail, jailTime attributes of this, changes the location of this'
	 * token.
	 * 
	 * @requires player and player's attached token are not null
	 * @modifies <b><tt>this</tt></b>, Token
	 * @effects token's position, so that player, is set to the Jail position.
	 *          player's jail time counter is set to the three. boolean field of
	 *          player, inJail, for checking player is in jail or not is set to
	 *          true.
	 *
	 */
	public void goToJail() {
		this.inJail = true;
		this.jailTime = 3;
		this.token.setLocation(GameController.getInstance().getBoard().getSquareLocationFromName("Jail"));
	}

	/**
	 * Called when player uses get out of jail card or rolls double in jail.
	 */
	public void getOutOfJail() {
		this.jailTime = 0;
		this.inJail = false;
	}

	/**
	 * @return The list of Cards player has.
	 */
	public List<OwnableCard> getCards() {
		return cards;
	}

	public List<OwnableCard> setCards(List<OwnableCard> list) {
		return this.cards = list;
	}

	/**
	 * Adds the given card to the this' cards if the cards is not null. cards is
	 * created and the given card is added if the cards is null.
	 * 
	 * @param card
	 *            The card to be added to player's list of cards.
	 * 
	 * @requires an OwnableCard type of card which is not null
	 * @modifies <b><tt>this</tt></b>
	 * @effects if this' cards are null, addCard creates a new list. addCard adds
	 *          card to this list.
	 * 
	 */
	public void addCard(OwnableCard card) {
		if (this.cards == null)
			cards = new ArrayList<>();
		this.cards.add(card);
	}

	/**
	 * @param card
	 *            The card to be removed from player's list of cards.
	 * @return <tt>true</tt> if card is removed from player's list of cards,
	 *         <tt>false</tt> if card was not in the list.
	 */
	public boolean removeCard(OwnableCard card) {
		if (this.cards == null)
			cards = new ArrayList<>();
		return this.cards.remove(card);
	}

	public void addProperty(OwnableSquare prop) {
		if (this.properties == null)
			properties = new ArrayList<>();
		
		List<OwnableSquare> oldVal = this.properties;
		this.properties.add(prop);
		publishPropertyEvent("properties", oldVal, this.properties);
	}

	public boolean removeProperty(OwnableSquare prop) {
		if (this.properties == null)
			properties = new ArrayList<>();
		
		List<OwnableSquare> oldVal = this.properties;
		
		if(this.properties.remove(prop)) {
			publishPropertyEvent("properties", oldVal, this.properties);
			return true;
		}
		
		return false;
	
	}

	/**
	 * @return the jailTime
	 */
	public int getJailTime() {
		return jailTime;
	}

	/**
	 * 
	 */
	public void decreaseJailTime() {
		if (this.jailTime > 0) {
			this.jailTime--;
			this.inJail = this.jailTime != 0;
		}
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token t) {
		this.token = t;
	}

	public boolean goBankrupt() {
		// TODO implement this
		return false;
	}

	public List<TitleDeedSquare> getTitleDeedsWithColor(TitleDeedSquareColor color) {
		List<TitleDeedSquare> result = new ArrayList<>();
		for (OwnableSquare ownable : this.properties) {
			if (ownable instanceof TitleDeedSquare) {
				if (((TitleDeedSquare) ownable).getColor() == color)
					result.add((TitleDeedSquare) ownable);
			}
		}
		return result;
	}

	public int getNumTitleDeedsWithColor(TitleDeedSquareColor color) {
		return getTitleDeedsWithColor(color).size();
	}

	public void setTotalMoney(int totalMoney) {
		this.totalMoney = totalMoney;

	}

	public void setInJail(boolean inJail) {
		this.inJail = inJail;
	}

	public void setJailTime(int jailTime) {
		this.jailTime = jailTime;
	}

	public List<OwnableSquare> getProperties() {
		return properties;
	}
	
	public void setProperties(List<OwnableSquare> newSq) {
		this.properties = newSq;
	}

	public boolean repOK() {
		if (nickName == null || nickName == "")
			return false;
		if (totalMoney < 0)
			return false;
		if (cards == null)
			return false;
		if (properties == null)
			return false;
		if (jailTime < 0 || jailTime > 3)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Player))
			return false;
		Player other = (Player) obj;
		if (nickName.equals(other.getNickName()))
			return true;
	
		//
		// NOT IMPORTANT 
		//
		
		if (inJail != other.inJail)
			return false;
		if (isReverseDirection != other.isReverseDirection)
			return false;
		if (jailTime != other.jailTime)
			return false;
		if (nickName == null) {
			if (other.nickName != null)
				return false;
		} else if (!nickName.equals(other.nickName))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (totalMoney != other.totalMoney)
			return false;

		return true;
	}

	public void refreshPropertyListeners() {
		propertyListenersMap = null;
	}
	
}
