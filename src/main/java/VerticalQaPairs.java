public class VerticalQaPairs {
        private String query;
    private String answer;
    private String type;
    private String replace_word;
    private float kelmn_score;

    public String getReplace_word() {
        return replace_word;
    }

    public void setReplace_word(String replace_word) {
        this.replace_word = replace_word;
    }

    public float getKelmn_score() {
        return kelmn_score;
    }

    public void setKelmn_score(float kelmn_score) {
        this.kelmn_score = kelmn_score;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VerticalQaPairs [query=" + query + ", answer=" + answer
                + ", type=" + type + ", replace_word=" + replace_word
                + ", kelmn_score=" + kelmn_score + "]";
    }
}
