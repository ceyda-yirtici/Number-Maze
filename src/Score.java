import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Score {
	private String name;
	private int score;
	
	public Score(String name, int score) throws IOException {
		this.name = name;
		this.score = score;
		
		
	}
	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
