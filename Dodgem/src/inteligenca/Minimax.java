package inteligenca;

import java.util.LinkedList;
import java.util.Random;

import javax.swing.SwingWorker;

import gui.GlavnoOkno;
import logika.Igra;
import logika.Igralec;
import logika.Poteza;

public class Minimax extends SwingWorker<Poteza, Object> { // Treba je implementirati to, da se na robu avtomobilcki odstranijo
	
	private GlavnoOkno master;
	private int globina;
	private Igralec jaz;
	
	public Minimax(GlavnoOkno master, int globina, Igralec jaz) {
		this.master = master;
		this.globina = globina;
		this.jaz = jaz;
	}
	
	@Override
	protected Poteza doInBackground() throws Exception {
		Igra igra = master.copyIgra();
		OcenjenaPoteza p = minimax(0, igra);
		for(int i = 0; i < 1; i++){
			Thread.sleep(500);
		}
		return p.poteza;
	}
	
	@Override
	public void done() {
		try {
			Poteza p = this.get();
			if (p != null) {
				master.odigraj(p); 
			}
		} catch (Exception e) {
		}
	}
	
	/**
	 * poi��e najbolj�o potezo v dani igri
	 */
	public OcenjenaPoteza minimax(int k, Igra igra) {
		// System.out.println("minimax, k: " + k);
		Igralec naPotezi = null;
		// Ugotovimo, ali je konec ali je kdo na potezi.
		switch (igra.stanje()) {
			case NA_POTEZI_VERTICAL: naPotezi = Igralec.VERTICAL; break;
			case NA_POTEZI_HORIZONTAL: naPotezi = Igralec.HORIZONTAL; break;
			// Igre je konec, ne moremo vrniti poteze, vrnemo le vrednost pozicije
			case ZMAGA_VERTICAL:
				return new OcenjenaPoteza(
						null,
						(jaz == Igralec.VERTICAL ? Ocena.ZMAGA : Ocena.ZGUBA));
			case ZMAGA_HORIZONTAL:
				return new OcenjenaPoteza(
						null,
						(jaz == Igralec.HORIZONTAL ? Ocena.ZMAGA : Ocena.ZGUBA));
		}
		assert (naPotezi != null);
		
		if (k >= globina) {
			return new OcenjenaPoteza(null, Ocena.oceniPozicijo(jaz, igra));
		}
		
		LinkedList<Poteza> najboljsePoteze = new LinkedList<Poteza>();
		int ocenaNajboljse = 0;
		for (Poteza p : igra.poteze()) {
			Igra kopijaIgre = new Igra(igra);
			kopijaIgre.odigraj(p);
			int ocenaP = minimax(k+1, kopijaIgre).vrednost;
			System.out.println(p.toString() + " ocenaP: " + ocenaP);
			if (najboljsePoteze.isEmpty() 
					|| (naPotezi == jaz && ocenaP > ocenaNajboljse)
					|| (naPotezi != jaz && ocenaP < ocenaNajboljse)
					) 
				{
				najboljsePoteze.clear();
				najboljsePoteze.add(p);
				ocenaNajboljse = ocenaP;
				}
		 else if (ocenaP == ocenaNajboljse) {
			najboljsePoteze.add(p);
		}
		}
		assert(! najboljsePoteze.isEmpty());
		Random r = new Random();
		Poteza najboljsa = najboljsePoteze.get(r.nextInt(najboljsePoteze.size()));
		return new OcenjenaPoteza(najboljsa, ocenaNajboljse);
		
		
	}


	
}