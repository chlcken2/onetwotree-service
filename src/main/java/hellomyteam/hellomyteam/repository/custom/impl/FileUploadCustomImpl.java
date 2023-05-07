package hellomyteam.hellomyteam.repository.custom.impl;

import com.querydsl.core.types.Path;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hellomyteam.hellomyteam.dto.ImgProfileResDto;
import hellomyteam.hellomyteam.dto.QImgProfileResDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.List;

import static hellomyteam.hellomyteam.entity.QImage.image;

@Repository
@RequiredArgsConstructor
public class FileUploadCustomImpl {
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public void updateProfileByTeamMemberInfoId(Long teamMemberInfoId, String imageUrl, String storeFilename) {
        queryFactory
                .update(image)
                .set(image.imageUrl, imageUrl)
                .set(image.storeFilename, storeFilename)
                .where(image.teamMemberInfo.id.eq(teamMemberInfoId))
                .execute();
    }


    public void updateLogoByTeam(Long teamId, String imageUrl, String storeFilename) {
        queryFactory
                .update(image)
                .set(image.imageUrl, imageUrl)
                .set(image.storeFilename, storeFilename)
                .where(image.team.id.eq(teamId))
                .execute();
    }

    public void changeImageByTeamId(Long teamId) {
        queryFactory
                .update(image)
                .set((Path<String>) image.imageUrl, (String) null)
                .set((Path<String>) image.storeFilename, (String) null)
                .where(image.team.id.eq(teamId))
                .execute();
    }

    public ImgProfileResDto getProfileImgByTmiId(Long teamMemberInfoId) {
        return queryFactory
                .select(new QImgProfileResDto(
                        image.teamMemberInfo.id,
                        image.imageUrl,
                        image.storeFilename,
                        image.createdDate
                ))
                .from(image)
                .where(image.teamMemberInfo.id.eq(teamMemberInfoId))
                .fetchOne();
    }
}
