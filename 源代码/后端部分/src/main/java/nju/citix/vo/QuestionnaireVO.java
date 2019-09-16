package nju.citix.vo;

import lombok.Data;
import nju.citix.po.Questionnaire;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * 问卷表单VO
 *
 * @author jiang hui
 * @date 2019/8/19
 */

@Data
public class QuestionnaireVO {

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
     * 问题回答的列表
     * 共18个问题
     */
    private List<Character> answers;

    /**
     * 问卷最后更新时间
     */
    private LocalDateTime lastUpdate;

    public QuestionnaireVO() {
    }

    public QuestionnaireVO(Questionnaire po) {
        questionnaireId = po.getQuestionnaireId();
        customerId = po.getCustomerId();
        lastUpdate = po.getLastUpdate();
        score = po.getScore();
        answers = new LinkedList<>();
        String ans = po.getAnswers();
        for (int i = 0; i < ans.length(); i++) {
            answers.add(ans.charAt(i));
        }
    }
}
