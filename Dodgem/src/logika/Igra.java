package logika;

import java.util.LinkedList;
import java.util.List;

public class Igra {
	/**
	 * Velikost igralne plo��e N x N.
	 */
	public static final int N = 5;

	/**
	 * Atributi objekta iz razreda igra.
	 */

	private Polje[][] plosca;
	private Igralec naPotezi;

	/**
	 * Nova igra, v za�etnem stanju so figurice prvega igralca (igralec X) 
	 * postavljene na dnu plo��e, figurice drugega igralca (igralec Y)
	 * pa na levi strani plo��e.
	 */
	public Igra() {
		plosca = new Polje[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (j == (N-1) && i != 0) {
					plosca[i][j] = Polje.X;
				}
				else {
					if (i == 0 && j != (N-1)) {
						plosca[i][j] = Polje.Y;
					}
					else {
						plosca[i][j] = Polje.PRAZNO;
					}
				}
			}
		}
		naPotezi = Igralec.X;
	}


	/**
	 * @param igra nova kopija dane igre
	 */
	public Igra(Igra igra) {
		plosca = new Polje[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				plosca[i][j] = igra.plosca[i][j];
			}
		}
		this.naPotezi = igra.naPotezi;
	}

	public Polje[][] getPlosca() {
		return plosca; 
	}


	/**
	 * @return seznam mo�nih potez za igralca, ki je na potezi
	 * Pazi na orientacijo osi!
	 */
	public List<Poteza> poteze() {
		LinkedList<Poteza> veljavnePoteze = new LinkedList<Poteza>();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (naPotezi == Igralec.X && plosca[i][j] == Polje.X) {
					if (i > 0 && plosca[i-1][j] == Polje.PRAZNO) {
						veljavnePoteze.add(new Poteza(i, j, Smer.LEVO));
					}
					if (i<N-1 && plosca[i+1][j] == Polje.PRAZNO) {
						veljavnePoteze.add(new Poteza(i, j, Smer.DESNO));
					}
					if ((j>0 && plosca[i][j-1] == Polje.PRAZNO) || (j==0)) { 
						veljavnePoteze.add(new Poteza(i, j, Smer.NAPREJ));
					}
				}
				if (naPotezi == Igralec.Y && plosca[i][j] == Polje.Y) {
					if (j>0 && plosca[i][j-1] == Polje.PRAZNO) {
						veljavnePoteze.add(new Poteza(i, j, Smer.LEVO));
					}
					if (j<N-1 && plosca[i][j+1] == Polje.PRAZNO) {
						veljavnePoteze.add(new Poteza(i, j, Smer.DESNO));
					}
					if ((i<N-1 && plosca[i+1][j] == Polje.PRAZNO) || (i==N-1)) {
						veljavnePoteze.add(new Poteza(i, j, Smer.NAPREJ));
					}
				}
			}
		}
		return veljavnePoteze;
	}


	/**
	 * @return stanje igre
	 */
	public Stanje stanje() {
		int steviloAvtomobilovX = 0;
		int steviloAvtomobilovY = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) { 
				// takoj, ko se eno od �tevil steviloAvtomobilov pove�a, ga ne rabimo ve� �teti, naprej bi lahko pregledovali le za drugega igralca
				if (plosca[i][j] == Polje.X) {
					steviloAvtomobilovX += 1;
				}
				if (plosca[i][j] == Polje.Y) {
					steviloAvtomobilovY +=1; 
				}
			}
		}
		if (steviloAvtomobilovX == 0) {
			return Stanje.ZMAGA_X;
		}
		else {
			if (steviloAvtomobilovY == 0) {
				return Stanje.ZMAGA_Y;
			}
			else {
				if (naPotezi == Igralec.X) {
					return Stanje.NA_POTEZI_X;
				}
				else {
					return Stanje.NA_POTEZI_Y;
				}
			}
		}
	}


	/**
	 * Odigraj potezo p.
	 * 
	 * @param p
	 * @return true, �e je bila poteza uspe�no odigrana
	 */
	public boolean odigraj(Poteza p) {
		if (plosca[p.getX()][p.getY()] == Polje.PRAZNO) {
			return false;
		}
		else {
			int x = p.getX();
			int y = p.getY();
			if (naPotezi == Igralec.X && plosca[x][y] == Polje.X) {
				switch(p.getSmer()) {					
				// Za vsako mo�no smer preverimo, �e je poteza dovoljena:
				// 1. ali je na polju (x, y) res X
				// 2. Ali smo na (levem/desnem/zgornjem) robu?
				// 3. �e nismo na robu, ali je na (levi/desni/naprej) prazno polje?
				// Izvedemo potezo: spremenimo obe polji (eno na prazno, eno na X), pri smeri NAPREJ lahko v dolo�eni situaciji spremenimo le eno polje
				// return true
				case LEVO:
					if (x == 0) { // smo na levem robu, ne moremo levo
						return false;
					}
					else { // nismo na levem robu
						if (plosca[x-1][y] != Polje.PRAZNO) { // ne moremo levo, ker polje tam ni prazno
							return false;
						}
						else { // polje na levi je prazno, izvedemo potezo
							plosca[x][y] = Polje.PRAZNO;
							plosca[x-1][y] = Polje.X;
							naPotezi = naPotezi.nasprotnik();
							return true;
						}
					}
				case DESNO:
					if (x == N-1) { // smo na desnem robu, ne moremo desno
						return false;
					}
					else { // nismo na desnem robu
						if (plosca[x+1][y] != Polje.PRAZNO) { // ne moremo desno, ker polje tam ni prazno
							return false;
						}
						else { // polje na desni je prazno, izvedemo potezo
							plosca[x][y] = Polje.PRAZNO;
							plosca[x+1][y] = Polje.X;
							naPotezi = naPotezi.nasprotnik();
							return true;
						}
					}
				case NAPREJ:
					if (y == 0) { // smo na zgornjem robu, premik naprej odstrani avtomobil�ek
						plosca[x][y] = Polje.PRAZNO;
						naPotezi = naPotezi.nasprotnik();
						return true;
					}
					else { // nismo na zgornjem robu
						if (plosca[x][y-1] != Polje.PRAZNO) { // ne moremo naprej, ker polje tam ni prazno
							return false;
						}
						else { // polje naprej je prazno, izvedemo potezo
							plosca[x][y] = Polje.PRAZNO;
							plosca[x][y-1] = Polje.X;
							naPotezi = naPotezi.nasprotnik();
							return true;
						}
					}
				}
			}
			else { // na potezi je igralec Y
				// Ali se lahko zgodi, da nimamo igralca?
				if (plosca[x][y] != Polje.Y) { // na polju (x, y) ni igralec Y
					return false;
				}
				else { // na polju (x, y) je avtomobil�ek, ki pripada Y
					switch(p.getSmer()) { 
					case LEVO:
						if (y == 0) { // smo na zgornjem (glede na orientacijo tega avta je to na levem) robu, ne moremo levo
							return false;
						}
						else { // nismo na levem robu
							if (plosca[x][y-1] != Polje.PRAZNO) { // ne moremo levo, ker polje tam ni prazno
								return false;
							}
							else { // polje na levi je prazno, izvedemo potezo
								plosca[x][y] = Polje.PRAZNO;
								plosca[x][y-1] = Polje.Y;
								naPotezi = naPotezi.nasprotnik();
								return true;
							}
						}
					case DESNO:
						if (y==N-1) { // smo na spodnjem (glede na orientacijo tega avta je to na desnem) robu, ne moremo desno
							return false;
						}
						else { // nismo na desnem robu
							if (plosca[x][y+1] != Polje.PRAZNO) { // ne moremo desno, ker polje tam ni prazno
								return false;
							}
							else { // polje na desni je prazno, izvedemo potezo
								plosca[x][y] = Polje.PRAZNO;
								plosca[x][y+1] = Polje.Y;
								naPotezi = naPotezi.nasprotnik();
								return true;
							}
						}
					case NAPREJ:
						if (x==N-1) { // smo na desnem robu, premik naprej odstrani avtomobil�ek
							plosca[x][y] = Polje.PRAZNO;
							naPotezi = naPotezi.nasprotnik();
							return true;
						}
						else { // nismo na desnem robu
							if (plosca[x+1][y] != Polje.PRAZNO) { //ne moremo naprej, ker polje tam ni prazno
								return false;
							}
							else { // polje naprej je prazno, izvedemo potezo
								plosca[x][y] = Polje.PRAZNO;
								plosca[x+1][y] = Polje.Y;
								naPotezi = naPotezi.nasprotnik();
								return true;
							}
						}
					}
				}
			}
		}
		return false; // Kdaj pridemo do tega?
	}
}

