*{ Display a post in one of these modes: 'full', 'home' or 'teaser' }*

<div class="post ${_as == 'teaser' ? 'teaser' : ''}">
   <h2 class="post-title">
      <a href="@{BlogViewer.show(_post.urlId)}">${_post.title}</a>
   </h2>
   <div class="post-metadata">
      <span class="post-author">&{'blog.by'} ${_post.reference.author}</span>,
      <span class="post-date">${_post.reference.postedAt.format('dd MMM yyyy')}</span>
      #{if _post.author != _post.reference.author}
      <span class="post-translator">, translated by ${_post.author}</span>
      <span class="post-translation-date">${_post.postedAt.format('dd MMM yy')}</span>
      #{/if}
      #{elseif _post.reference.tags}
      <span class="post-tags">
         - Tagged
         #{list items:_post.reference.tags, as:'tag'}
         <a href="@{BlogViewer.listTagged(tag.name)}">${tag}</a>${tag_isLast ? '' : ', '}
         #{/list}
      </span>
      #{/elseif}
   </div>
   #{if _as != 'teaser'}
   <div class="post-content">
      ${_post.content.nl2br()}
   </div>
   #{/if}
</div>

#{if _as == 'full'}
#{if _isConnected}
<div class="translateMe">
   <a href="@{BlogController.translate(_post.urlId, _post.language)}">Translate me !</a>
   &nbsp;|&nbsp;
   <a href="@{BlogController.edit(_post.urlId, _post.language)}">Edit me !</a>
   &nbsp;|&nbsp;
   <a href="@{BlogController.deletePost_confirm(_post.urlId, _post.language)}">Delete me !</a>
</div>
#{/if}
#{/if}
