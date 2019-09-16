package nju.citix.po;

import lombok.Data;
import nju.citix.vo.QuestionnaireForm;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * 问卷表单
 *
 * @author jiang hui
 * @date 2019/8/19
 */

@Data
public class Questionnaire {

    /**
     * 问卷id
     */
    private Integer questionnaireId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 消费者id
     */
    private Integer customerId;

    /**
     * 问题回答的字符串形式，用来和数据库对接
     */
    private String answers;

    /**
     * 问卷最后更新时间
     */
    private LocalDateTime lastUpdate;

    public Questionnaire() {
    }

    public Questionnaire(QuestionnaireForm form) {
        customerId = form.getCustomerId();
        ArrayList<Character> answerList = form.getAnswerList();
        lastUpdate = LocalDateTime.now();
        score = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < answerList.size(); i++) {
            Character c = answerList.get(i);
            if (i == 0 || i == 10 || i == 11) {
                score += (c - 'A') * 2 + 1;
            } else {
                score += c - 'A' + 1;
            }
            sb.append(c);
        }
        answers = sb.toString();
    }

    public void updateAnswersByForm(QuestionnaireForm form) {
        ArrayList<Character> answerList = form.getAnswerList();
        lastUpdate = LocalDateTime.now();
        score = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < answerList.size(); i++) {
            Character c = answerList.get(i);
            if (i == 0 || i == 10 || i == 11) {
                score += (c - 'A') * 2 + 1;
            } else {
                score += c - 'A' + 1;
            }
            sb.append(c);
        }
        answers = sb.toString();
    }

}
