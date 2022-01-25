package com.easemob.im.server.api.group;

import com.easemob.im.server.api.Context;
import com.easemob.im.server.api.group.admin.GroupAdminAdd;
import com.easemob.im.server.api.group.admin.GroupAdminList;
import com.easemob.im.server.api.group.admin.GroupAdminRemove;
import com.easemob.im.server.api.group.announcement.GroupAnnouncement;
import com.easemob.im.server.api.group.create.CreateGroup;
import com.easemob.im.server.api.group.delete.DeleteGroup;
import com.easemob.im.server.api.group.get.GetGroup;
import com.easemob.im.server.api.group.list.GroupList;
import com.easemob.im.server.api.group.member.add.GroupMemberAdd;
import com.easemob.im.server.api.group.member.list.GroupMemberList;
import com.easemob.im.server.api.group.member.remove.GroupMemberRemove;
import com.easemob.im.server.api.group.settings.UpdateGroup;
import com.easemob.im.server.api.group.settings.UpdateGroupRequest;
import com.easemob.im.server.model.EMGroup;
import com.easemob.im.server.model.EMPage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

/**
 * 群API。
 */
public class GroupApi {

    private GroupList groupList;
    private CreateGroup createGroup;
    private DeleteGroup deleteGroup;

    private GetGroup getGroup;
    private UpdateGroup updateGroup;

    private GroupAnnouncement groupAnnouncement;
    private GroupMemberList groupMemberList;
    private GroupMemberAdd groupMemberAdd;
    private GroupMemberRemove groupMemberRemove;

    private GroupAdminList groupAdminList;
    private GroupAdminAdd groupAdminAdd;
    private GroupAdminRemove groupAdminRemove;

    public GroupApi(Context context) {
        this.groupList = new GroupList(context);
        this.createGroup = new CreateGroup(context);
        this.deleteGroup = new DeleteGroup(context);

        this.getGroup = new GetGroup(context);
        this.updateGroup = new UpdateGroup(context);

        this.groupAnnouncement = new GroupAnnouncement(context);

        this.groupMemberList = new GroupMemberList(context);
        this.groupMemberAdd = new GroupMemberAdd(context);
        this.groupMemberRemove = new GroupMemberRemove(context);

        this.groupAdminList = new GroupAdminList(context);
        this.groupAdminAdd = new GroupAdminAdd(context);
        this.groupAdminRemove = new GroupAdminRemove(context);
    }

    /**
     * 创建公开群。
     * <p>
     * 需要注意的是，目前公开群不允许成员邀请其他用户加入。如果要允许，可以用修改群API设置:
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * List<String> members = new ArrayList<>();
     * members.add("userA");
     * try {
     *     String groupId = service.group().createPublicGroup("owner", "groupName", "description", members, 200, true).block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * <pre>{@code
     * EMService service;
     * try {
     *     // 修改群组API，允许成员邀请其他用户加入
     *     service.group().updateSetting("group-id", settings -> settings.memberCanInvite(true)).block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param owner             群主的用户名
     * @param groupName         群名
     * @param description       群介绍
     * @param members           初始群成员的用户名列表
     * @param maxMembers        群最大成员数
     * @param needApproveToJoin 新成员加入需要管理员审批
     * @return 群id或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E5%88%9B%E5%BB%BA%E4%B8%80%E4%B8%AA%E7%BE%A4%E7%BB%84">创建群</a>
     */
    public Mono<String> createPublicGroup(String owner, String groupName, String description,
            List<String> members, int maxMembers, boolean needApproveToJoin) {
        return this.createGroup
                .publicGroup(owner, groupName, description, members, maxMembers, needApproveToJoin);
    }

    /**
     * 创建私有群。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * List<String> members = new ArrayList<>();
     * members.add("userA");
     * try {
     *     String groupId = service.group().privateGroup("owner", "groupName", "description", members, 200, true).block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param owner           群主的用户名
     * @param groupName       群名
     * @param description     群介绍
     * @param members         初始群成员的用户名列表
     * @param maxMembers      群最大成员数
     * @param canMemberInvite 新成员加入需要管理员审批
     * @return 群id或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E5%88%9B%E5%BB%BA%E4%B8%80%E4%B8%AA%E7%BE%A4%E7%BB%84">创建群</a>
     */
    public Mono<String> createPrivateGroup(String owner, String groupName, String description,
            List<String> members, int maxMembers, boolean canMemberInvite) {
        return this.createGroup
                .privateGroup(owner, groupName, description, members, maxMembers, canMemberInvite);
    }

    /**
     * 注销群。
     * <p>
     * 请谨慎使用。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     String groupId = service.group().destroyGroup("groupId").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId 群id
     * @return 成功或错误
     */
    public Mono<Void> destroyGroup(String groupId) {
        return this.deleteGroup.execute(groupId);
    }

    /**
     * 获取全部群列表。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     List<String> groups = service.group().listAllGroups().collectList().block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @return 每个群id或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E8%8E%B7%E5%8F%96app%E4%B8%AD%E6%89%80%E6%9C%89%E7%9A%84%E7%BE%A4%E7%BB%84_%E5%8F%AF%E5%88%86%E9%A1%B5">获取群列表</a>
     */
    public Flux<String> listAllGroups() {
        return this.groupList.all(20);
    }

    /**
     * 分页获取群列表。
     * <p>
     * 初次调用时，{@code cursor} 传 {@code null}。之后的调用，{@code cursor} 传上次返回的值。
     * <p>
     * API使用示例：
     * <pre>{@code
     * EMPage<String> page = null;
     * try {
     *     page = service.group().listGroups(10, null).block();
     *     List<String> groupIds = page.getValues();
     *     System.out.println("群组列表:" + groupIds);
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     *
     * // ... do something with the groupIds ...
     * if (page != null) {
     *      String cursor = page.getCursor();
     *      // cursor == null indicates the end of the list
     *      while (cursor != null) {
     *          try {
     *              page = service.group().listGroups(10, cursor).block();
     *              System.out.println("群组列表:" + page.getValues());
     *              // ... do something to the groupIds ...
     *              cursor = page.getCursor();
     *          } catch (EMException e) {
     *              e.getErrorCode();
     *              e.getMessage();
     *          }
     *      }
     * }
     * }</pre>
     *
     * @param limit  每次取回多少个群id
     * @param cursor 上次返回的{@code cursor}
     * @return 群列表响应或错误
     * @see com.easemob.im.server.model.EMPage
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E8%8E%B7%E5%8F%96app%E4%B8%AD%E6%89%80%E6%9C%89%E7%9A%84%E7%BE%A4%E7%BB%84_%E5%8F%AF%E5%88%86%E9%A1%B5">获取群列表</a>
     */
    public Mono<EMPage<String>> listGroups(int limit, String cursor) {
        return this.groupList.next(limit, cursor);
    }

    /**
     * 获取用户加入的所有群组。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     List<String> groups = service.group().listGroupsUserJoined("username").collectList().block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param username the username
     * @return A {@code Flux} which emits {@code EMGroup} on successful.
     */
    public Flux<String> listGroupsUserJoined(String username) {
        return this.groupList.userJoined(username);
    }

    /**
     * 获取群详情。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     EMGroup group = service.group().getGroup("groupId").block();
     *     String groupName = group.getName();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId 群id
     * @return 群详情或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E8%8E%B7%E5%8F%96%E7%BE%A4%E7%BB%84%E8%AF%A6%E6%83%85">获取群详情</a>
     */
    public Mono<EMGroup> getGroup(String groupId) {
        return this.getGroup.execute(groupId);
    }

    /**
     * 修改群详情。
     * <p>
     * 支持修改的参数见{@code GroupSettingsUpdateRequest}
     * <p>
     * API使用示例：
     * <p>
     * 比如，更新群最大成员数：
     * <pre>{@code
     * EMService service;
     * try {
     *     service.group().updateSettings("1", settings -> settings.maxMembers(100)).block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId    群id
     * @param customizer 请求定制器
     * @return 成功或错误
     * @see UpdateGroupRequest
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E4%BF%AE%E6%94%B9%E7%BE%A4%E7%BB%84%E4%BF%A1%E6%81%AF">修改群详情</a>
     */
    public Mono<Void> updateGroup(String groupId, Consumer<UpdateGroupRequest> customizer) {
        return this.updateGroup.update(groupId, customizer);
    }

    /**
     * 修改群主。新群主需要已经是群成员，否则会报错{@code EMForbiddenException}。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     service.group().updateGroupOwner("groupId", "username").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId  群id
     * @param username 新群主的用户名
     * @return 成功或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E8%BD%AC%E8%AE%A9%E7%BE%A4%E7%BB%84">修改群主</a>
     */
    public Mono<Void> updateGroupOwner(String groupId, String username) {
        return this.updateGroup.updateOwner(groupId, username);
    }

    /**
     * 获取群公告。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     String groupAnnouncement = service.group().getGroupAnnouncement("groupId").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId 群id
     * @return 群公告或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E8%8E%B7%E5%8F%96%E7%BE%A4%E7%BB%84%E5%85%AC%E5%91%8A">获取群公告</a>
     */
    public Mono<String> getGroupAnnouncement(String groupId) {
        return this.groupAnnouncement.get(groupId);
    }

    /**
     * 更新群公告。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     service.group().updateGroupAnnouncement("groupId", "announcement").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId      群id
     * @param announcement 群公告
     * @return 成功或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E4%BF%AE%E6%94%B9%E7%BE%A4%E7%BB%84%E5%85%AC%E5%91%8A">更新群公告</a>
     */
    public Mono<Void> updateGroupAnnouncement(String groupId, String announcement) {
        return this.groupAnnouncement.set(groupId, announcement);
    }

    /**
     * 获取群全部成员。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     List<String> members = service.group().listAllGroupMembers("username").collectList().block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId 群id
     * @return 每个群成员或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E5%88%86%E9%A1%B5%E8%8E%B7%E5%8F%96%E7%BE%A4%E7%BB%84%E6%88%90%E5%91%98">获取群成员</a>
     */
    public Flux<String> listAllGroupMembers(String groupId) {
        return this.groupMemberList.all(groupId, 20);
    }

    /**
     * 分页获取群成员。
     * <p>
     * API使用示例：
     * <p>
     * 首次调用时，{@code cursor} 传 {@code null}。之后每次调用，{@code cursor} 传上次返回的值。
     * <p>
     * 比如：
     *
     * <pre>{@code
     * EMService service;
     * EMPage<String> page = null;
     * try {
     *     page = service.group().listGroupMembers(groupId, 1, null).block();
     *     List<String> members = page.getValues();
     *     System.out.println("群组成员列表:" + members);
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     *
     * // ... do something to the members ...
     * if (page != null) {
     *      String cursor = page.getCursor();
     *      while (cursor != null) {
     *              try {
     *              page = service.group().listGroupMembers(groupId, 1, cursor).block();
     *              System.out.println("群组成员列表:" + page.getValues());
     *              // ... do something to the members ...
     *              cursor = page.getCursor();
     *          } catch (EMException e) {
     *              e.getErrorCode();
     *              e.getMessage();
     *          }
     *      }
     * }
     * }</pre>
     *
     * @param groupId 群id
     * @param limit   返回多少群成员id
     * @param cursor  开始位置
     * @return 获取群成员响应或错误
     * @see com.easemob.im.server.api.group.member.list.GroupMemberListResponse
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E5%88%86%E9%A1%B5%E8%8E%B7%E5%8F%96%E7%BE%A4%E7%BB%84%E6%88%90%E5%91%98">获取群成员</a>
     */
    public Mono<EMPage<String>> listGroupMembers(String groupId, int limit, String cursor) {
        return this.groupMemberList.next(groupId, limit, cursor);
    }

    /**
     * 添加群成员。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     service.group().addGroupMember("groupId", "username").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId  群id
     * @param username 要添加的用户的用户名
     * @return 成功或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E6%B7%BB%E5%8A%A0%E5%8D%95%E4%B8%AA%E7%BE%A4%E7%BB%84%E6%88%90%E5%91%98">添加群成员</a>
     */
    public Mono<Void> addGroupMember(String groupId, String username) {
        return this.groupMemberAdd.single(groupId, username);
    }

    /**
     * 移除群成员。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     service.group().removeGroupMember("groupId", "username").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId  群id
     * @param username 要移除的用户的用户名
     * @return 成功或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E7%A7%BB%E9%99%A4%E5%8D%95%E4%B8%AA%E7%BE%A4%E7%BB%84%E6%88%90%E5%91%98">移除群成员</a>
     */
    public Mono<Void> removeGroupMember(String groupId, String username) {
        return this.groupMemberRemove.single(groupId, username);
    }

    /**
     * 获取群全部管理员。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     List<String> admins = service.group().listGroupAdmins("groupId").collectList().block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId 群id
     * @return 每个管理员或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E8%8E%B7%E5%8F%96%E7%BE%A4%E7%AE%A1%E7%90%86%E5%91%98%E5%88%97%E8%A1%A8">获取群管理员</a>
     */
    public Flux<String> listGroupAdmins(String groupId) {
        return this.groupAdminList.all(groupId);
    }

    /**
     * 升级群成员为群管理员。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     service.group().addGroupAdmin("groupId", "username").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId  群id
     * @param username 被升级的群成员的用户名
     * @return 成功或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E6%B7%BB%E5%8A%A0%E7%BE%A4%E7%AE%A1%E7%90%86%E5%91%98">升级群成员</a>
     */
    public Mono<Void> addGroupAdmin(String groupId, String username) {
        return this.groupAdminAdd.single(groupId, username);
    }

    /**
     * 降级群管理员为群成员。
     * <p>
     * API使用示例：
     * <pre> {@code
     * EMService service;
     * try {
     *     service.group().removeGroupAdmin("groupId", "username").block();
     * } catch (EMException e) {
     *     e.getErrorCode();
     *     e.getMessage();
     * }
     * }</pre>
     *
     * @param groupId  群id
     * @param username 被降级的群管理员的用户名
     * @return 成功或错误
     * @see <a href="http://docs-im.easemob.com/im/server/basics/group#%E7%A7%BB%E9%99%A4%E7%BE%A4%E7%AE%A1%E7%90%86%E5%91%98">降级群管理员</a>
     */
    public Mono<Void> removeGroupAdmin(String groupId, String username) {
        return this.groupAdminRemove.single(groupId, username);
    }

}
