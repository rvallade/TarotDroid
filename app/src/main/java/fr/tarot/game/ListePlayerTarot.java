package fr.tarot.game;

import java.util.ArrayList;
import java.util.List;

public class ListePlayerTarot extends ArrayList<PlayerTarot> {
	private static final long serialVersionUID = 1L;

	private PlayerTarot preneur = null;
	
	public PlayerTarot getPreneur(){
		return preneur;
	}
	
	public void setPreneur(int idPreneur){
		preneur = getPlayerById(idPreneur);
		preneur.setPreneur(true);
	}
	
	/**
	 * Renvoit la liste des joueurs qui jouent apres.
	 * @param index
	 * @return
	 */
	public List<PlayerTarot> getNextPlayers(int index){
		return super.subList(index+1, size());
	}
	
	public PlayerTarot getPlayerById(int idPlayer){
		for (PlayerTarot player:this){
			if (player.getIdPlayer()==idPlayer) return player;
		}
		return null;
	}
	public void reInit(){
	    preneur = null;
	}
}
